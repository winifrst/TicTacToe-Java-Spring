package org.tictactoe.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Информация о завершенной игре для истории")
public class GameHistoryResponse {
    @Schema(description = "ID игры", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID gameId;

    @Schema(description = "Статус игры", example = "PLAYER_X_WON")
    private String status;

    @Schema(description = "Символ игрока в этой игре", example = "X")
    private String playerSymbol;

    @Schema(description = "Результат для игрока", example = "WIN")
    private String result;

    @Schema(description = "UUID противника", example = "223e4567-e89b-12d3-a456-426614174001")
    private UUID opponentId;

    @Schema(description = "Имя противника", example = "player2")
    private String opponentUsername;

    @Schema(description = "Символ противника", example = "O")
    private String opponentSymbol;

    @Schema(description = "Дата создания игры", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Игра против компьютера", example = "false")
    private boolean againstComputer;

    // Геттеры и сеттеры
    public UUID getGameId() { return gameId; }
    public void setGameId(UUID gameId) { this.gameId = gameId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPlayerSymbol() { return playerSymbol; }
    public void setPlayerSymbol(String playerSymbol) { this.playerSymbol = playerSymbol; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public UUID getOpponentId() { return opponentId; }
    public void setOpponentId(UUID opponentId) { this.opponentId = opponentId; }

    public String getOpponentUsername() { return opponentUsername; }
    public void setOpponentUsername(String opponentUsername) { this.opponentUsername = opponentUsername; }

    public String getOpponentSymbol() { return opponentSymbol; }
    public void setOpponentSymbol(String opponentSymbol) { this.opponentSymbol = opponentSymbol; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isAgainstComputer() { return againstComputer; }
    public void setAgainstComputer(boolean againstComputer) { this.againstComputer = againstComputer; }
}