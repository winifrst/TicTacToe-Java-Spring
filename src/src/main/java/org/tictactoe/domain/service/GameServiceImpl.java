package org.tictactoe.domain.service;

import org.tictactoe.datasource.repository.GameRepository;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository repository;

    public GameServiceImpl(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    public int[] getNextMove(Game game) {
        int[][] board = game.getBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

//    @Override
//    public boolean validateBoard(Game game, int[][] newBoard) {
//        int[][] oldBoard = game.getBoard();
//        int changes = 0;
//
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (oldBoard[i][j] != newBoard[i][j]) {
//                    changes++;
//                    if (oldBoard[i][j] != 0 || newBoard[i][j] != 1) {
//                        return false;
//                    }
//                }
//            }
//        }
//        return changes == 1;
//    }

    @Override
    public boolean validateBoard(Game game, int[][] newBoard) {
        // проверяем, что сделан ровно один ход
        int movesMade = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (newBoard[i][j] != 0) {
                    movesMade++;
                    // Проверяем, что стоит только 1 (игрок) или 2 (компьютер)
                    if (newBoard[i][j] != 1 && newBoard[i][j] != 2) {
                        return false;
                    }
                }
            }
        }

        // Для начала игры допускаем 1 ход
        return movesMade == 1;
    }

    @Override
    public GameStatus checkGameStatus(Game game) {
        int[][] board = game.getBoard();

        // Проверка строк и столбцов
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0] == 1 ? GameStatus.PLAYER_WON : GameStatus.COMPUTER_WON;
            }
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
            newBoard[move[0]][move[1]] = 2;
            game.setBoard(newBoard);
            game.setPlayerTurn(true);
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