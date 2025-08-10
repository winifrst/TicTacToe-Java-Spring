package org.tictactoe.domain.service;

import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;

public interface GameService {
    int[] getNextMove(Game game);
    boolean validateBoard(Game game, int[][] newBoard);
    GameStatus checkGameStatus(Game game);
}