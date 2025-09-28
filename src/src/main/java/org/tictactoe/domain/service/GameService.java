package org.tictactoe.domain.service;

import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;

public interface GameService {
    int[] getNextMove(Game game);  // ход компьютера
    boolean validateBoard(Game game, int[][] newBoard);  // проверка хода
    GameStatus checkGameStatus(Game game);  // проверка состояния
    Game makeComputerMove(Game game);  // выполнить ход компьютера
}