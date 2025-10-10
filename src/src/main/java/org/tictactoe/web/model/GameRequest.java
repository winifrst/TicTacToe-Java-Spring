package org.tictactoe.web.model;

public class GameRequest {
    private int[][] board;

    // Геттеры и сеттеры
    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }
}