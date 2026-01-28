package org.tictactoe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tictactoe.domain.service.AuthService;
import org.tictactoe.web.model.AuthResponse;
import org.tictactoe.web.model.JwtRequest;
import org.tictactoe.web.model.JwtResponse;
import org.tictactoe.web.model.SignUpRequest;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Tag(name = "Аутентификация", description = "Регистрация и авторизация пользователей")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя в системе. Логин должен быть уникальным."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно зарегистрирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Успешная регистрация",
                                    value = "{\"message\": \"User registered successfully\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Логин уже существует",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Ошибка регистрации",
                                    value = "{\"message\": \"Username already exists\"}"
                            )
                    )
            )
    })
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
    @Operation(
            summary = "Авторизация пользователя",
            description = "Аутентификация пользователя с использованием Basic Authentication. " +
                    "Логин и пароль должны быть закодированы в base64 в формате login:password"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Успешный вход",
                                    value = "{\"id\": \"123e4567-e89b-12d3-a456-426614174000\", \"message\": \"Authentication successful\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверные учетные данные",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Ошибка аутентификации",
                                    value = "{\"message\": \"Invalid credentials\"}"
                            )
                    )
            )
    })
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
}