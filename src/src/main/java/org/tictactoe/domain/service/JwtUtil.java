package org.tictactoe.domain.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.tictactoe.domain.model.JwtAuthentication;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    public JwtAuthentication generateAuthentication(Claims claims) {
        String userIdStr = claims.get("userId", String.class);
        String username = claims.get("username", String.class);

        // Важно: извлекаем роли из claims


        List<String> roles = claims.get("roles", List.class);
        if (roles == null) {
            roles = Collections.singletonList("ROLE_USER");
        }

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UUID userId = UUID.fromString(userIdStr);
        JwtAuthentication authentication = new JwtAuthentication();
        authentication.setUserId(userId);
        authentication.setUsername(username);
        authentication.setAuthorities(authorities);
        authentication.setAuthenticated(true);

        return authentication;
    }
}