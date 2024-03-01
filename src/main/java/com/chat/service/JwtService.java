package com.chat.service;

import com.chat.model.Token;
import com.chat.repository.TokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
@Component
public class JwtService {

    private static final long EXPIRATION_TIME = 1_000 * 3_600 * 12;
    @Value("${key}")
    private String SECRET;

    public static final String BEARER = "Bearer ";

    private final TokenRepository tokenRepository;

    @Autowired
    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateJwt(String subject) {
        String token = Jwts.builder()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(signedKey())
                .compact();

        tokenRepository.save(Token.builder().token(token).build());
        return token;
    }

    public String extractSubject(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(token == null) {
            return null;
        }

        if(token.startsWith(BEARER)) {
            token = token.substring(BEARER.length());
        }


        return Jwts.parser()
                .setSigningKey(signedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private Key signedKey() {
        byte[] bytes = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(bytes);
    }

}
