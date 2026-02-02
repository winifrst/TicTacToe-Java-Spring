package org.tictactoe.domain.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.tictactoe.domain.model.Role;
import org.tictactoe.domain.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    @Value("${jwt.secret.access}")
    private String jwtAccessSecret;

    @Value("${jwt.secret.refresh}")
    private String jwtRefreshSecret;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpiration;

    // Метод генерации accessToken по User
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("username", user.getUsername());

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(getSignInKey(jwtAccessSecret), SignatureAlgorithm.HS256)
                .compact();
    }

    // Метод генерации refreshToken по User
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("tokenType", "refresh");

        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(getSignInKey(jwtRefreshSecret), SignatureAlgorithm.HS256)
                .compact();
    }

    // Метод валидации accessToken
    public boolean validateAccessToken(String token) {
        return validateToken(token, jwtAccessSecret);
    }

    // Метод валидации refreshToken
    public boolean validateRefreshToken(String token) {
        return validateToken(token, jwtRefreshSecret);
    }

    // Метод получения claims
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(jwtAccessSecret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            try {
                return Jwts.parserBuilder()
                        .setSigningKey(getSignInKey(jwtRefreshSecret))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } catch (Exception ex) {
                throw new RuntimeException("Invalid JWT token", ex);
            }
        }
    }

    // Дополнительные методы для получения claims по типу токена
    public Claims getAccessClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(jwtAccessSecret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid access token", e);
        }
    }

    public Claims getRefreshClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(jwtRefreshSecret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token", e);
        }
    }

    private boolean validateToken(String token, String secret) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(secret))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    private SecretKey getSignInKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUserIdFromToken(String token) {
        Claims claims = getAccessClaims(token);
        return claims.get("userId", String.class);
    }
}