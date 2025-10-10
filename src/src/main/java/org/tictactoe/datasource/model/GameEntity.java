package org.tictactoe.datasource.model;

import java.util.UUID;

public class GameEntity {
    private UUID id;
    private int[][] board;
    private boolean isPlayerTurn;

    // Конструкторы
    public GameEntity() {}

    public GameEntity(UUID id, int[][] board, boolean isPlayerTurn) {
        this.id = id;
        this.board = board;
        this.isPlayerTurn = isPlayerTurn;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public int[][] getBoard() { return board; }
    public void setBoard(int[][] board) { this.board = board; }

    public boolean isPlayerTurn() { return isPlayerTurn; }
    public void setPlayerTurn(boolean playerTurn) { isPlayerTurn = playerTurn; }
}