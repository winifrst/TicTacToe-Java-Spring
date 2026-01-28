package org.tictactoe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tictactoe.domain.service.UserService;
import org.tictactoe.web.model.UserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@Tag(
        name = "Пользователи",
        description = "Операции с пользователями: получение информации"
)
@SecurityRequirement(name = "basicAuth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Получить информацию о пользователе по ID",
            description = "Возвращает основную информацию о пользователе: ID и логин."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Информация о пользователе",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(
                                    name = "Пример ответа",
                                    value = """
                    {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "username": "testuser"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable UUID userId) {
        try {
            return userService.findById(userId)
                    .map(user -> {
                        UserResponse response = new UserResponse();
                        response.setId(user.getId());
                        response.setUsername(user.getUsername());
                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/me")
    @Operation(
            summary = "Получить информацию о текущем пользователе",
            description = "Возвращает информацию о текущем аутентифицированном пользователе."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Информация о текущем пользователе",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getCurrentUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UUID) {
                UUID userId = (UUID) auth.getPrincipal();
                return getUserInfo(userId);
            }
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}