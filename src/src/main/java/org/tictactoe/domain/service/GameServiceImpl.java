package org.tictactoe.domain.service;

import org.tictactoe.domain.model.Game;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {
    // ... другие методы

    @Override
    public int[] getNextMove(Game game) {
        int[][] board = game.getBoard();
        int[] bestMove = new int[]{-1, -1};
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = 2; // Ход компьютера
                    int score = minimax(board, 0, false);
                    board[i][j] = 0; // Отмена хода

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }

        return bestMove;
    }

    private int minimax(int[][] board, int depth, boolean isMaximizing) {
        int result = evaluate(board);
        if (result != 0) return result;
        if (isBoardFull(board)) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == 0) {
                        board[i][j] = 2;
                        int score = minimax(board, depth + 1, false);
                        board[i][j] = 0;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == 0) {
                        board[i][j] = 1;
                        int score = minimax(board, depth + 1, true);
                        board[i][j] = 0;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private int evaluate(int[][] board) {
        // Проверка строк
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                if (board[i][0] == 1) return -10;
                else if (board[i][0] == 2) return 10;
            }
        }

        // Проверка столбцов
        for (int j = 0; j < 3; j++) {
            if (board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                if (board[0][j] == 1) return -10;
                else if (board[0][j] == 2) return 10;
            }
        }

        // Проверка диагоналей
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == 1) return -10;
            else if (board[0][0] == 2) return 10;
        }

        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == 1) return -10;
            else if (board[0][2] == 2) return 10;
        }

        return 0;
    }

    private boolean isBoardFull(int[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) return false;
            }
        }
        return true;
    }
}