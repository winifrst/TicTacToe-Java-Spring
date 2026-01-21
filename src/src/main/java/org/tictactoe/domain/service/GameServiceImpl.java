package org.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;

import java.util.UUID;

import static org.tictactoe.domain.service.Constants.*;

@Service
public class GameServiceImpl implements GameService {

    @Override
    public boolean validateMove(Game game, int row, int col, UUID playerId) {
        // Проверяем, что игра активна
        if (game.getStatus() != GameStatus.PLAYER_X_TURN &&
                game.getStatus() != GameStatus.PLAYER_O_TURN &&
                game.getStatus() != GameStatus.COMPUTER_TURN) {
            return false;
        }

        // Проверяем, что это ход игрока
        if (!isPlayerTurn(game, playerId)) {
            return false;
        }

        // Проверяем границы доски
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            return false;
        }

        // Проверяем, что клетка пуста
        if (game.getBoard()[row][col] != EMPTY) {
            return false;
        }

        // Проверяем, что игрок участвует в игре
        return isPlayerInGame(game, playerId);
    }

    @Override
    public boolean validateBoard(Game game, int[][] newBoard) {
        // Проверяем, что не изменены предыдущие ходы
        int[][] oldBoard = game.getBoard();
        int movesAdded = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // Если в старом поле было значение, оно не должно измениться
                if (oldBoard[i][j] != EMPTY && oldBoard[i][j] != newBoard[i][j]) {
                    return false;
                }
                // Считаем добавленные ходы
                if (oldBoard[i][j] == EMPTY && newBoard[i][j] != EMPTY) {
                    movesAdded++;
                }
            }
        }

        // Должен быть добавлен ровно один ход
        return movesAdded == 1;
    }

    @Override
    public Game makeMove(Game game, int row, int col, UUID playerId) {
        int playerSymbol = game.getPlayerSymbolCode(playerId);

        // Обновляем доску
        int[][] newBoard = copyBoard(game.getBoard());
        newBoard[row][col] = playerSymbol;
        game.setBoard(newBoard);

        // Обновляем статус игры
        updateGameStatusAfterMove(game, playerSymbol);

        return game;
    }

    @Override
    public Game makeComputerMove(Game game) {
        int[] move = getNextMove(game);

        if (move[0] != -1) {
            int[][] newBoard = copyBoard(game.getBoard());
            newBoard[move[0]][move[1]] = SECOND_PLAYER;
            game.setBoard(newBoard);

            updateGameStatusAfterMove(game, SECOND_PLAYER);
        }

        return game;
    }

    @Override
    public Game checkGameStatus(Game game) {
        int[][] board = game.getBoard();
        int winner = checkWinner(board);

        if (winner == FIRST_PLAYER) {
            game.setStatus(GameStatus.PLAYER_X_WON);
        } else if (winner == SECOND_PLAYER) {
            game.setStatus(GameStatus.PLAYER_O_WON);
        } else if (isBoardFull(board)) {
            game.setStatus(GameStatus.DRAW);
        }

        return game;
    }

    private int checkWinner(int[][] board) {
        // Проверка строк
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] != EMPTY &&
                    board[i][0] == board[i][1] &&
                    board[i][1] == board[i][2]) {
                return board[i][0];
            }
        }

        // Проверка столбцов
        for (int j = 0; j < BOARD_SIZE; j++) {
            if (board[0][j] != EMPTY &&
                    board[0][j] == board[1][j] &&
                    board[1][j] == board[2][j]) {
                return board[0][j];
            }
        }

        // Проверка диагоналей
        if (board[0][0] != EMPTY &&
                board[0][0] == board[1][1] &&
                board[1][1] == board[2][2]) {
            return board[0][0];
        }
        if (board[0][2] != EMPTY &&
                board[0][2] == board[1][1] &&
                board[1][1] == board[2][0]) {
            return board[0][2];
        }

        return EMPTY;
    }

    private void updateGameStatusAfterMove(Game game, int playerSymbol) {
        // Сначала проверяем статус игры
        checkGameStatus(game);

        // Если игра еще не закончена, обновляем очередь хода
        if (game.getStatus() == GameStatus.PLAYER_X_TURN ||
                game.getStatus() == GameStatus.PLAYER_O_TURN ||
                game.getStatus() == GameStatus.COMPUTER_TURN) {

            if (game.isAgainstComputer()) {
                if (playerSymbol == FIRST_PLAYER && game.getStatus() != GameStatus.PLAYER_X_WON) {
                    // После хода игрока - ход компьютера
                    game.setStatus(GameStatus.COMPUTER_TURN);
                    game.setCurrentPlayerId(null);
                } else if (playerSymbol == SECOND_PLAYER && game.getStatus() != GameStatus.PLAYER_O_WON) {
                    // После хода компьютера - ход игрока
                    game.setStatus(GameStatus.PLAYER_X_TURN);
                    game.setCurrentPlayerId(game.getPlayerXId());
                }
            } else {
                if (playerSymbol == FIRST_PLAYER && game.getStatus() != GameStatus.PLAYER_X_WON) {
                    // После хода игрока X - ход игрока O
                    game.setStatus(GameStatus.PLAYER_O_TURN);
                    game.setCurrentPlayerId(game.getPlayerOId());
                } else if (playerSymbol == SECOND_PLAYER && game.getStatus() != GameStatus.PLAYER_O_WON) {
                    // После хода игрока O - ход игрока X
                    game.setStatus(GameStatus.PLAYER_X_TURN);
                    game.setCurrentPlayerId(game.getPlayerXId());
                }
            }
        }
    }

    @Override
    public int[] getNextMove(Game game) {
        int[][] board = copyBoard(game.getBoard());

        // Используем Minimax для нахождения лучшего хода
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = SECOND_PLAYER;
                    int score = minimax(board, 0, false);
                    board[i][j] = EMPTY;

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

    // Алгоритм Minimax
    private int minimax(int[][] board, int depth, boolean isMaximizing) {
        int winner = checkWinner(board);

        if (winner == SECOND_PLAYER) return 10 - depth;
        if (winner == FIRST_PLAYER) return depth - 10;
        if (isBoardFull(board)) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = SECOND_PLAYER;
                        int score = minimax(board, depth + 1, false);
                        board[i][j] = EMPTY;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = FIRST_PLAYER;
                        int score = minimax(board, depth + 1, true);
                        board[i][j] = EMPTY;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    @Override
    public boolean isPlayerInGame(Game game, UUID playerId) {
        return game.isPlayerX(playerId) || game.isPlayerO(playerId);
    }

    @Override
    public int getPlayerSymbol(Game game, UUID playerId) {
        return game.getPlayerSymbolCode(playerId);
    }

    @Override
    public boolean isPlayerTurn(Game game, UUID playerId) {
        if (game.getCurrentPlayerId() == null) {
            return false;
        }

        return game.getCurrentPlayerId().equals(playerId) &&
                (game.getStatus() == GameStatus.PLAYER_X_TURN ||
                        game.getStatus() == GameStatus.PLAYER_O_TURN);
    }

    private boolean isBoardFull(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, BOARD_SIZE);
        }
        return newBoard;
    }
}