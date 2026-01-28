package org.tictactoe.domain.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.*;

public class User {
    private UUID id;
    private String username;
    private String password;
//    private Set<Role> roles = new HashSet<>();
private Role role = Role.USER;  // Одна роль

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    // Геттер/сеттер для роли
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

//    public void addRole(Role role) {
//        this.roles.add(role);
//    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public Set<Role> getRoles() {
//        return roles;
//    }

//    public void setRoles(Set<Role> roles) {
//        this.roles = roles;
//    }

}
