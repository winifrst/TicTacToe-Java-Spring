package org.tictactoe.web.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class GameResponse {
    private String gameId;
    private int[][] board;
    private String status;
    private UUID playerXId;
    private UUID playerOId;
    private UUID currentPlayerId;
    private boolean againstComputer;
    private String playerSymbol;  // X, O или null
    private LocalDateTime createdAt;
    private String playerXSymbol;  // Добавлено
    private String playerOSymbol;  // Добавлено

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