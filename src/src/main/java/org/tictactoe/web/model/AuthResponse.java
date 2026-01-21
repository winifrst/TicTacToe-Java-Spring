package org.tictactoe.web.model;

import java.util.UUID;

public class AuthResponse {
    private UUID id;

    private String message;

    public UUID getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
