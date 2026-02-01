package org.tictactoe.domain.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public class JwtAuthentication implements Authentication {

    private boolean authenticated;
    private UUID userId;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    // Конструкторы
    public JwtAuthentication() {
        this.authenticated = false;
    }

    public JwtAuthentication(UUID userId, String username,
                             Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
        this.authenticated = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null; // В JWT нет credentials
    }

    @Override
    public Object getDetails() {
        return null; // Можно вернуть дополнительную информацию
    }

    @Override
    public Object getPrincipal() {
        return userId; // Возвращаем UUID пользователя
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return userId != null ? userId.toString() : null;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}