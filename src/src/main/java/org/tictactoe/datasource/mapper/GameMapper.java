package org.tictactoe.datasource.mapper;

import org.tictactoe.datasource.model.GameEntity;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;

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

    private static String boardToString(int[][] board) {
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
        game.setStatus(entity.getStatus());
        game.setPlayerXId(entity.getPlayerXId());
        game.setPlayerOId(entity.getPlayerOId());
        game.setCurrentPlayerId(entity.getCurrentPlayerId());
        game.setAgainstComputer(entity.isAgainstComputer());
        game.setCreatedAt(entity.getCreatedAt());
        game.setPlayerXSymbol(entity.getPlayerXSymbol());
        game.setPlayerOSymbol(entity.getPlayerOSymbol());

        return game;
    }

    public static GameEntity toEntity(Game domain) {
        GameEntity entity = new GameEntity();
        entity.setId(domain.getId());
        entity.setBoard(boardToString(domain.getBoard()));
        entity.setStatus(domain.getStatus());
        entity.setPlayerXId(domain.getPlayerXId());
        entity.setPlayerOId(domain.getPlayerOId());
        entity.setCurrentPlayerId(domain.getCurrentPlayerId());
        entity.setAgainstComputer(domain.isAgainstComputer());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setPlayerXSymbol(domain.getPlayerXSymbol());
        entity.setPlayerOSymbol(domain.getPlayerOSymbol());

        return entity;
    }
}