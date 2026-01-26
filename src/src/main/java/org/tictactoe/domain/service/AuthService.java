package org.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import org.tictactoe.domain.model.User;
import org.tictactoe.web.model.SignUpRequest;

import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {
    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public UUID register(SignUpRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return null;
        }

        User user = userService.createUser(request.getUsername(), request.getPassword());
        return user.getId();
    }

    public UUID authenticate(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return null;
        }

        try {
            String base64Credentials = authHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] values = credentials.split(":", 2);

            if (values.length != 2) {
                return null;
            }

            String username = values[0];
            String password = values[1];

            if (userService.validateUser(username, password)) {
                return userService.findByUsername(username)
                        .map(User::getId)
                        .orElse(null);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }
}