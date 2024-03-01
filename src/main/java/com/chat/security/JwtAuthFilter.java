package com.chat.security;

import com.chat.repository.TokenRepository;
import com.chat.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@AllArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String username = jwtService.extractSubject(request);
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(token != null && token.startsWith(JwtService.BEARER)) {
            token = token.substring(JwtService.BEARER.length());
        }

        if(username != null && tokenRepository.existsByToken(token)) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username, null, Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
