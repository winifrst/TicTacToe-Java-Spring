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

//    @PostMapping("/signup")
//    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest request) {
//        boolean success = authService.register(request);
//
//        AuthResponse response = new AuthResponse();
//        if (success) {
//            response.setMessage("User registered successfully");
//            return ResponseEntity.ok(response);
//        } else {
//            response.setMessage("Username already exists");
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestHeader("Authorization") String authHeader) {
//        UUID userId = authService.authenticate(authHeader);
//
//        AuthResponse response = new AuthResponse();
//        if (userId != null) {
//            response.setId(userId);
//            response.setMessage("Authentication successful");
//            return ResponseEntity.ok(response);
//        } else {
//            response.setMessage("Invalid credentials");
//            return ResponseEntity.status(401).body(response);
//        }
//    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest request) {
        try {
            System.out.println("=== SIGNUP DEBUG ===");
            System.out.println("Username from request: " + request.getUsername());
            System.out.println("Password from request: " + request.getPassword());

            boolean success = authService.register(request);
            System.out.println("AuthService.register result: " + success);

            AuthResponse response = new AuthResponse();
            if (success) {
                response.setMessage("User registered successfully");
                System.out.println("Returning success");
                return ResponseEntity.ok(response);
            } else {
                response.setMessage("Username already exists");
                System.out.println("Returning username exists error");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("ERROR in signup: " + e.getMessage());
            e.printStackTrace();

            AuthResponse response = new AuthResponse();
            response.setMessage("Server error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("=== LOGIN DEBUG ===");
            System.out.println("Auth header: " + authHeader);

            UUID userId = authService.authenticate(authHeader);

            AuthResponse response = new AuthResponse();
            if (userId != null) {
                response.setId(userId);
                response.setMessage("Authentication successful");
                System.out.println("Login successful for userId: " + userId);
                return ResponseEntity.ok(response);
            } else {
                response.setMessage("Invalid credentials");
                System.out.println("Login failed - invalid credentials");
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            System.err.println("ERROR in login: " + e.getMessage());
            e.printStackTrace();

            AuthResponse response = new AuthResponse();
            response.setMessage("Server error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}