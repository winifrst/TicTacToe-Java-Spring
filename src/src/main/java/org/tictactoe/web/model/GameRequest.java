package org.tictactoe.web.model;

public class GameRequest {
    private int[][] board;

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }
}