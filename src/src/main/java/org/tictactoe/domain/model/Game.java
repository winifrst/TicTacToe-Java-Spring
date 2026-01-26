package org.tictactoe.domain.model;

import org.tictactoe.domain.service.Constants;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.tictactoe.domain.service.Constants.BOARD_SIZE;

public class Game {
    private UUID id;
    private int[][] board;
    private GameStatus status;
    private UUID playerXId;
    private UUID playerOId;
    private UUID currentPlayerId;
    private boolean isAgainstComputer;
    private LocalDateTime createdAt;
    private String playerXSymbol;
    private String playerOSymbol;

    public Game() {
        this.id = UUID.randomUUID();
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        this.status = GameStatus.WAITING_FOR_PLAYERS;
        this.isAgainstComputer = false;
        this.createdAt = LocalDateTime.now();
        this.playerXSymbol = "X";
        this.playerOSymbol = "O";
    }


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public int[][] getBoard() { return board; }
    public void setBoard(int[][] board) { this.board = board; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public UUID getPlayerXId() { return playerXId; }
    public void setPlayerXId(UUID playerXId) { this.playerXId = playerXId; }

    public UUID getPlayerOId() { return playerOId; }
    public void setPlayerOId(UUID playerOId) { this.playerOId = playerOId; }

    public UUID getCurrentPlayerId() { return currentPlayerId; }
    public void setCurrentPlayerId(UUID currentPlayerId) { this.currentPlayerId = currentPlayerId; }

    public boolean isAgainstComputer() { return isAgainstComputer; }
    public void setAgainstComputer(boolean againstComputer) { isAgainstComputer = againstComputer; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPlayerXSymbol() { return playerXSymbol; }
    public void setPlayerXSymbol(String playerXSymbol) { this.playerXSymbol = playerXSymbol; }

    public String getPlayerOSymbol() { return playerOSymbol; }
    public void setPlayerOSymbol(String playerOSymbol) { this.playerOSymbol = playerOSymbol; }

    public boolean isPlayerX(UUID playerId) {
        return playerId != null && playerId.equals(playerXId);
    }

    public boolean isPlayerO(UUID playerId) {
        return playerId != null && playerId.equals(playerOId);
    }

    public int getPlayerSymbolCode(UUID playerId) {
        if (isPlayerX(playerId)) return Constants.FIRST_PLAYER;
        if (isPlayerO(playerId)) return Constants.SECOND_PLAYER;
        return Constants.EMPTY;  // если не участвует в игре
    }

    public String getPlayerSymbolString(UUID playerId) {
        if (isPlayerX(playerId)) return playerXSymbol;
        if (isPlayerO(playerId)) return playerOSymbol;
        return null;
    }
}