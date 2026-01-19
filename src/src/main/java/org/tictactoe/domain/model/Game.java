package org.tictactoe.domain.model;

import java.util.UUID;

import static org.tictactoe.domain.service.Constants.*;

public class Game {
    private UUID id;
    private int[][] board;
    private boolean isPlayerTurn;
    private UUID userId;


    public Game() {
        this.id = UUID.randomUUID();
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        this.isPlayerTurn = true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int[][] getBoard() {
        return board;
    }

    public UUID getUserId() { return userId; }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        isPlayerTurn = playerTurn;
    }

    public void setUserId(UUID userId) { this.userId = userId; }
}