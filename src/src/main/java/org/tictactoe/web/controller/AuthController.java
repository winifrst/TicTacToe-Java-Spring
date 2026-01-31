package org.tictactoe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tictactoe.domain.service.AuthService;
import org.tictactoe.web.model.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Аутентификация", description = "Регистрация и авторизация пользователей")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Регистрация нового пользователя")
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
    @Operation(summary = "Авторизация пользователя (получение токенов)")
    public ResponseEntity<?> login(@RequestBody JwtRequest request) {
        try {
            JwtResponse jwtResponse = authService.login(request);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Invalid credentials: " + e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @PostMapping("/refresh/access")
    @Operation(summary = "Обновление access токена")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshJwtRequest request) {
        try {
            JwtResponse jwtResponse = authService.getNewAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Invalid refresh token: " + e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление refresh токена")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshJwtRequest request) {
        try {
            JwtResponse jwtResponse = authService.getNewRefreshToken(request.getRefreshToken());
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMessage("Invalid refresh token: " + e.getMessage());
            return ResponseEntity.status(401).body(errorResponse);
        }
    }
}