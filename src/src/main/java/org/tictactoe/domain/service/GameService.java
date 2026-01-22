package org.tictactoe.domain.service;

import org.tictactoe.domain.model.Game;

import java.util.UUID;

public interface GameService {
    int[] getNextMove(Game game);

    boolean validateBoard(Game game, int[][] newBoard, UUID playerId);

    Game checkGameStatus(Game game);

    boolean validateMove(Game game, int row, int col, UUID playerId);

    Game makeMove(Game game, int row, int col, UUID playerId);

    Game makeComputerMove(Game game);

    boolean isPlayerInGame(Game game, UUID playerId);

    int getPlayerSymbol(Game game, UUID playerId);

    boolean isPlayerTurn(Game game, UUID playerId);
}