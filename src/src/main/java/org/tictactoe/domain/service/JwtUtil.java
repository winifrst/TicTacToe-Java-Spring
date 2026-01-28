package org.tictactoe.domain.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.tictactoe.domain.model.JwtAuthentication;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    public JwtAuthentication generateAuthentication(Claims claims) {
        // Извлекаем данные из claims
        String userIdStr = claims.get("userId", String.class);
        String username = claims.get("username", String.class);

        // Получаем роли
        List<String> roles = claims.get("roles", List.class);
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Создаём аутентификацию
        UUID userId = UUID.fromString(userIdStr);
        JwtAuthentication authentication = new JwtAuthentication(userId, username, authorities);
        authentication.setAuthenticated(true);

        return authentication;
    }

    // Метод для получения userId из claims
    public UUID getUserId(Claims claims) {
        String userIdStr = claims.get("userId", String.class);
        return UUID.fromString(userIdStr);
    }

    // Метод для получения username из claims
    public String getUsername(Claims claims) {
        return claims.get("username", String.class);
    }
}