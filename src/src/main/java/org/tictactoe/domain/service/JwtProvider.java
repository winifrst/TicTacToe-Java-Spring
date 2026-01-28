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

    // Секретные ключи
    @Value("${jwt.secret.access}")
    private String jwtAccessSecret;

    @Value("${jwt.secret.refresh}")
    private String jwtRefreshSecret;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpiration;

    // Генерация Access Token
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("username", user.getUsername());

        // Используем getAuthorities() который мы добавили в User
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

    // Генерация Refresh Token
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

    // Валидация Access Token
    public boolean validateAccessToken(String token) {
        return validateToken(token, jwtAccessSecret);
    }

    // Валидация Refresh Token
    public boolean validateRefreshToken(String token) {
        return validateToken(token, jwtRefreshSecret);
    }

    // Получение Claims из Access Token
    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtAccessSecret);
    }

    // Получение Claims из Refresh Token
    public Claims getRefreshClaims(String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    // Получение User ID из токена
    public UUID getUserIdFromToken(String token) {
        Claims claims = getAccessClaims(token);
        return UUID.fromString(claims.get("userId", String.class));
    }

    // Получение ролей из токена
    public List<String> getRolesFromToken(String token) {
        Claims claims = getAccessClaims(token);
        return claims.get("roles", List.class);
    }

    private boolean validateToken(String token, String secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey(secret))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Токен невалидный (истёк, неправильная подпись и т.д.)
            System.err.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token, String secret) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSignInKey(String secret) {
        // Преобразуем строку в SecretKey для HS256
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}