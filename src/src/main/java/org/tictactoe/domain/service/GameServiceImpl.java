package org.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;

@Service
public class GameServiceImpl implements GameService {

    @Override
    public int[] getNextMove(Game game) {
        // Пока простая логика - ищем первую пустую клетку
        int[][] board = game.getBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};  // нет ходов
    }

    @Override
    public boolean validateBoard(Game game, int[][] newBoard) {
        int[][] oldBoard = game.getBoard();
        int changes = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (oldBoard[i][j] != newBoard[i][j]) {
                    changes++;
                    // Проверяем, что игрок поставил только один символ X (1)
                    if (oldBoard[i][j] != 0 || newBoard[i][j] != 1) {
                        return false;
                    }
                }
            }
        }
        return changes == 1;  // должно быть ровно одно изменение
    }

    @Override
    public GameStatus checkGameStatus(Game game) {
        int[][] board = game.getBoard();

        // Проверка строк и столбцов
        for (int i = 0; i < 3; i++) {
            // Проверка строк
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0] == 1 ? GameStatus.PLAYER_WON : GameStatus.COMPUTER_WON;
            }
            // Проверка столбцов
            if (board[0][i] != 0 && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return board[0][i] == 1 ? GameStatus.PLAYER_WON : GameStatus.COMPUTER_WON;
            }
        }

        // Проверка диагоналей
        if (board[0][0] != 0 && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0] == 1 ? GameStatus.PLAYER_WON : GameStatus.COMPUTER_WON;
        }
        if (board[0][2] != 0 && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2] == 1 ? GameStatus.PLAYER_WON : GameStatus.COMPUTER_WON;
        }

        // Проверка на ничью
        boolean isBoardFull = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    isBoardFull = false;
                    break;
                }
            }
        }

        return isBoardFull ? GameStatus.DRAW : GameStatus.IN_PROGRESS;
    }

    @Override
    public Game makeComputerMove(Game game) {
        int[] move = getNextMove(game);
        if (move[0] != -1) {
            int[][] newBoard = copyBoard(game.getBoard());
            newBoard[move[0]][move[1]] = 2;  // компьютер ставит O
            game.setBoard(newBoard);
            game.setPlayerTurn(true);  // следующий ход - игрока
        }
        return game;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, 3);
        }
        return newBoard;
    }
}