package org.tictactoe.web.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Ответ при операциях аутентификации")
public class AuthResponse {
    @Schema(
            description = "UUID пользователя (только для успешной авторизации)",
            example = "123e4567-e89b-12d3-a456-426614174000",
            nullable = true
    )
    private UUID id;

    @Schema(
            description = "Сообщение о результате операции",
            example = "Authentication successful"
    )
    private String message;

    public UUID getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}