package org.tictactoe.web.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Ответ с информацией об игре")
public class GameResponse {
    @Schema(
            description = "UUID игры",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private String gameId;

    @Schema(
            description = "Игровое поле (3x3 матрица), где 0 - пусто, 1 - игрок X, 2 - игрок O",
            example = "[[0,0,1],[0,2,0],[0,0,0]]"
    )
    private int[][] board;

    @Schema(
            description = "Текущий статус игры",
            example = "PLAYER_X_TURN",
            allowableValues = {
                    "WAITING_FOR_PLAYERS", "PLAYER_X_TURN", "PLAYER_O_TURN",
                    "COMPUTER_TURN", "PLAYER_X_WON", "PLAYER_O_WON", "DRAW"
            }
    )
    private String status;

    @Schema(
            description = "UUID игрока X",
            example = "123e4567-e89b-12d3-a456-426614174000",
            nullable = true
    )
    private UUID playerXId;

    @Schema(
            description = "UUID игрока O",
            example = "223e4567-e89b-12d3-a456-426614174001",
            nullable = true
    )
    private UUID playerOId;

    @Schema(
            description = "UUID текущего игрока (чей ход)",
            example = "123e4567-e89b-12d3-a456-426614174000",
            nullable = true
    )
    private UUID currentPlayerId;

    @Schema(
            description = "Игра против компьютера",
            example = "true"
    )
    private boolean againstComputer;

    @Schema(
            description = "Символ текущего пользователя в этой игре (X или O)",
            example = "X",
            nullable = true
    )
    private String playerSymbol;

    @Schema(
            description = "Дата и время создания игры",
            example = "2024-01-15T10:30:00"
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "Символ игрока X (всегда 'X')",
            example = "X"
    )
    private String playerXSymbol;

    @Schema(
            description = "Символ игрока O (всегда 'O')",
            example = "O"
    )
    private String playerOSymbol;

    // Геттеры и сеттеры
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    public int[][] getBoard() { return board; }
    public void setBoard(int[][] board) { this.board = board; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getPlayerXId() { return playerXId; }
    public void setPlayerXId(UUID playerXId) { this.playerXId = playerXId; }

    public UUID getPlayerOId() { return playerOId; }
    public void setPlayerOId(UUID playerOId) { this.playerOId = playerOId; }

    public UUID getCurrentPlayerId() { return currentPlayerId; }
    public void setCurrentPlayerId(UUID currentPlayerId) { this.currentPlayerId = currentPlayerId; }

    public boolean isAgainstComputer() { return againstComputer; }
    public void setAgainstComputer(boolean againstComputer) { this.againstComputer = againstComputer; }

    public String getPlayerSymbol() { return playerSymbol; }
    public void setPlayerSymbol(String playerSymbol) { this.playerSymbol = playerSymbol; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPlayerXSymbol() { return playerXSymbol; }
    public void setPlayerXSymbol(String playerXSymbol) { this.playerXSymbol = playerXSymbol; }

    public String getPlayerOSymbol() { return playerOSymbol; }
    public void setPlayerOSymbol(String playerOSymbol) { this.playerOSymbol = playerOSymbol; }
}