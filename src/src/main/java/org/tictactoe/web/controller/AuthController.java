package org.tictactoe.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tictactoe.domain.service.AuthService;
import org.tictactoe.web.model.AuthResponse;
import org.tictactoe.web.model.SignUpRequest;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest request) {
        boolean success = authService.register(request);

        AuthResponse response = new AuthResponse();
        if (success) {
            response.setMessage("User registered successfully");
            return ResponseEntity.ok(response);
        } else {
            response.setMessage("Username already exists");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestHeader("Authorization") String authHeader) {
        UUID userId = authService.authenticate(authHeader);

        AuthResponse response = new AuthResponse();
        if (userId != null) {
            response.setId(userId);
            response.setMessage("Authentication successful");
            return ResponseEntity.ok(response);
        } else {
            response.setMessage("Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }
}