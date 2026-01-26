package org.tictactoe.web.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на регистрацию нового пользователя")
public class SignUpRequest {

    @Schema(
            description = "Логин пользователя (должен быть уникальным)",
            example = "testuser",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1
    )
    private String username;

    @Schema(
            description = "Пароль пользователя",
            example = "password123",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1
    )
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
