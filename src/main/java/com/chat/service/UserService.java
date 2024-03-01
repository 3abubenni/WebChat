package com.chat.service;

import com.chat.model.Token;
import com.chat.model.User;
import com.chat.model.request.UserLoginRequest;
import com.chat.model.request.UserPatchRequest;
import com.chat.model.request.UserRegisterRequest;
import com.chat.model.response.TokenResponse;
import com.chat.model.response.UserResponse;
import com.chat.repository.TokenRepository;
import com.chat.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private JwtService jwtService;
    private PasswordEncoder encoder;
    private AuthenticationManager authenticationManager;
    private TokenRepository tokenRepository;


    public TokenResponse register(UserRegisterRequest request) {
        if(!userRepository.existsByEmail(request.getEmail())) {
            User user = User.builder()
                    .email(request.getEmail())
                    .name(request.getName())
                    .password(encoder.encode(request.getPassword()))
                    .build();

            userRepository.save(user);
            return new TokenResponse(jwtService.generateJwt(request.getEmail()));
        }
        return null;
    }

    public TokenResponse login(UserLoginRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
        );
        Authentication auth = authenticationManager.authenticate(token);
        return new TokenResponse(jwtService.generateJwt(request.getEmail()));
    }

    public void logout(HttpServletRequest request) {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(jwt.startsWith(JwtService.BEARER)) {
            jwt = jwt.substring(JwtService.BEARER.length());
        }
        Token token = tokenRepository.findByToken(jwt).orElseThrow();
        tokenRepository.delete(token);
    }

    public UserResponse me(HttpServletRequest request) {
        User user = userRepository.findByEmail(jwtService.extractSubject(request)).orElseThrow();
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public List<UserResponse> searchUserByEmail(String email) {
        List<User> users = userRepository.findAll();
        users = users.stream()
                .filter(user -> user.getEmail().startsWith(email) && user.getEmail().length() - email.length() < 10)
                .toList();

        List<UserResponse> userResponseList = new ArrayList<>(users.size());
        for(User user: users) {
            userResponseList.add(
                    UserResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .build()
            );
        }
        return userResponseList;
    }

    public void patchUser(UserPatchRequest userPatchRequest, HttpServletRequest request) {
        User user = userRepository.findByEmail(jwtService.extractSubject(request)).orElseThrow();
        user.setName(userPatchRequest.getName());
        user.setPassword(encoder.encode(userPatchRequest.getPassword()));
        userRepository.save(user);
    }

}
