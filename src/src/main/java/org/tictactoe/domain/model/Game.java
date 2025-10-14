package org.tictactoe.domain.model;

import java.util.UUID;

public class Game {
    private UUID id;
    private int[][] board;
    private boolean isPlayerTurn;

    public Game() {
        this.id = UUID.randomUUID();
        this.board = new int[3][3];
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

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        isPlayerTurn = playerTurn;
    }
}