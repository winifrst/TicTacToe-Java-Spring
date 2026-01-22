package org.tictactoe.datasource.model;

import jakarta.persistence.*;
import org.tictactoe.domain.model.GameStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "games")
public class GameEntity {
    @Id
    private UUID id;

    @Column(name = "board", nullable = false)
    private String board;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameStatus status;

    @Column(name = "player_x_id")
    private UUID playerXId;

    @Column(name = "player_o_id")
    private UUID playerOId;

    @Column(name = "current_player_id")
    private UUID currentPlayerId;

    @Column(name = "is_against_computer", nullable = false)
    private boolean isAgainstComputer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "player_x_symbol", nullable = false)
    private String playerXSymbol = "X";

    @Column(name = "player_o_symbol", nullable = false)
    private String playerOSymbol = "O";

    public GameEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public UUID getPlayerXId() {
        return playerXId;
    }

    public void setPlayerXId(UUID playerXId) {
        this.playerXId = playerXId;
    }

    public UUID getPlayerOId() {
        return playerOId;
    }

    public void setPlayerOId(UUID playerOId) {
        this.playerOId = playerOId;
    }

    public UUID getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(UUID currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public boolean isAgainstComputer() {
        return isAgainstComputer;
    }

    public void setAgainstComputer(boolean againstComputer) {
        isAgainstComputer = againstComputer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPlayerXSymbol() {
        return playerXSymbol;
    }

    public void setPlayerXSymbol(String playerXSymbol) {
        this.playerXSymbol = playerXSymbol;
    }

    public String getPlayerOSymbol() {
        return playerOSymbol;
    }

    public void setPlayerOSymbol(String playerOSymbol) {
        this.playerOSymbol = playerOSymbol;
    }
}