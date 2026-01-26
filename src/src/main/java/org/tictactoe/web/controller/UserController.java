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
import org.springframework.web.bind.annotation.*;
import org.tictactoe.domain.service.UserService;
import org.tictactoe.web.model.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable UUID userId) {
        return userService.findById(userId)
                .map(user -> {
                    UserResponse response = new UserResponse();
                    response.setId(user.getId());
                    response.setUsername(user.getUsername());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
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
    public ResponseEntity<UserResponse> getCurrentUser(HttpServletRequest request) {
        try {
            UUID userId = (UUID) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }
            return getUserInfo(userId);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}