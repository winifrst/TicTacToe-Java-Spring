package org.tictactoe.datasource.mapper;

import org.tictactoe.datasource.model.GameEntity;
import org.tictactoe.domain.model.Game;

import static org.tictactoe.domain.service.Constants.BOARD_SIZE;

public class GameMapper {

    private static int[][] stringToBoard(String stringBoard) {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        String[] values = stringBoard.split(",");
        int index = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = Integer.parseInt(values[index++]);
            }
        }

        return board;
    }

    private static String boardToString(int[][] board) {  // храним в БД в виде строки
        StringBuilder stringBoard = new StringBuilder();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                stringBoard.append(board[i][j]);
                if (i != BOARD_SIZE - 1 || j != BOARD_SIZE - 1) stringBoard.append(",");
            }
        }

        return stringBoard.toString();
    }

    public static Game toDomain(GameEntity entity) {
        Game game = new Game();
        game.setId(entity.getId());
        game.setBoard(stringToBoard(entity.getBoard()));
        game.setPlayerTurn(entity.isPlayer1Turn());
        return game;
    }

    public static GameEntity toEntity(Game domain) {
        GameEntity entity = new GameEntity();
        entity.setId(domain.getId());
        entity.setBoard(boardToString(domain.getBoard()));
        entity.setPlayerTurn(domain.isPlayerTurn());

        return entity;
    }
}