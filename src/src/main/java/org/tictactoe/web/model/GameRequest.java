package org.tictactoe.web.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на выполнение хода в игре")
public class GameRequest {
    @Schema(
            description = "Новое состояние игрового поля (3x3 матрица)",
            example = "[[0,0,1],[0,2,0],[0,0,0]]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int[][] board;

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }
}