package org.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;

import java.util.UUID;

import static org.tictactoe.domain.service.Constants.*;

@Service
public class GameServiceImpl implements GameService {

    @Override
    public boolean validateBoard(Game game, int[][] newBoard, UUID playerId) {
        int[][] oldBoard = game.getBoard();
        int changes = 0;
        int row = -1, col = -1;
        int newValue = EMPTY;
        printBoard(oldBoard);

        printBoard(newBoard);

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (oldBoard[i][j] != newBoard[i][j]) {
                    changes++;
                    row = i;
                    col = j;
                    newValue = newBoard[i][j];
                    if (oldBoard[i][j] != EMPTY) {
                        return false;
                    }
                }
            }
        }
        if (changes != 1) {
            return false;
        }

        int expectedSymbol = game.getPlayerSymbolCode(playerId);
        if (newValue != expectedSymbol) {
            return false;
        }

        boolean isTurn = isPlayerTurn(game, playerId);
        return isTurn;
    }

    @Override
    public boolean isPlayerTurn(Game game, UUID playerId) {
        if (game.getCurrentPlayerId() == null) {
            return false;
        }

        boolean result = false;

        if (game.getStatus() == GameStatus.PLAYER_X_TURN) {
            boolean isPlayerX = game.isPlayerX(playerId);
            boolean isCurrentPlayer = game.getCurrentPlayerId().equals(playerId);
            result = isPlayerX && isCurrentPlayer;
        } else if (game.getStatus() == GameStatus.PLAYER_O_TURN) {
            boolean isPlayerO = game.isPlayerO(playerId);
            boolean isCurrentPlayer = game.getCurrentPlayerId().equals(playerId);
            result = isPlayerO && isCurrentPlayer;
        } else if (game.getStatus() == GameStatus.COMPUTER_TURN) {

            result = false;
        } else {

        }
        return result;
    }

    @Override
    public Game makeMove(Game game, int row, int col, UUID playerId) {
        int playerSymbol = game.getPlayerSymbolCode(playerId);
        int[][] newBoard = copyBoard(game.getBoard());
        newBoard[row][col] = playerSymbol;
        game.setBoard(newBoard);
        printBoard(game.getBoard());

        if (game.isAgainstComputer()) {
            if (playerSymbol == FIRST_PLAYER) {
                game.setStatus(GameStatus.COMPUTER_TURN);
                game.setCurrentPlayerId(null);

            } else {
                game.setStatus(GameStatus.PLAYER_X_TURN);
                game.setCurrentPlayerId(game.getPlayerXId());

            }
        } else {
            if (playerSymbol == FIRST_PLAYER) {
                game.setStatus(GameStatus.PLAYER_O_TURN);
                game.setCurrentPlayerId(game.getPlayerOId());
            } else {
                game.setStatus(GameStatus.PLAYER_X_TURN);
                game.setCurrentPlayerId(game.getPlayerXId());
            }
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

    @Override
    public boolean validateMove(Game game, int row, int col, UUID playerId) {
        if (game.getStatus() != GameStatus.PLAYER_X_TURN &&
                game.getStatus() != GameStatus.PLAYER_O_TURN &&
                game.getStatus() != GameStatus.COMPUTER_TURN) {
            return false;
        }

        if (!isPlayerTurn(game, playerId)) {
            return false;
        }

        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            return false;
        }

        if (game.getBoard()[row][col] != EMPTY) {
            return false;
        }
        return isPlayerInGame(game, playerId);
    }

    @Override
    public Game makeComputerMove(Game game) {

        int[] move = getNextMove(game);

        if (move[0] != -1) {
            int[][] newBoard = copyBoard(game.getBoard());
            newBoard[move[0]][move[1]] = SECOND_PLAYER;
            game.setBoard(newBoard);

            game.setStatus(GameStatus.PLAYER_X_TURN);
            game.setCurrentPlayerId(game.getPlayerXId());
        }

        return game;
    }

    @Override
    public int[] getNextMove(Game game) {
        int[][] board = copyBoard(game.getBoard());
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

    @Override
    public boolean isPlayerInGame(Game game, UUID playerId) {
        return game.isPlayerX(playerId) || game.isPlayerO(playerId);
    }

    @Override
    public int getPlayerSymbol(Game game, UUID playerId) {
        return game.getPlayerSymbolCode(playerId);
    }

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

    private int checkWinner(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] != EMPTY &&
                    board[i][0] == board[i][1] &&
                    board[i][1] == board[i][2]) {
                return board[i][0];
            }
        }

        for (int j = 0; j < BOARD_SIZE; j++) {
            if (board[0][j] != EMPTY &&
                    board[0][j] == board[1][j] &&
                    board[1][j] == board[2][j]) {
                return board[0][j];
            }
        }

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

    private void printBoard(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            System.out.print("[");
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j]);
                if (j < board[i].length - 1) System.out.print(", ");
            }

        }
    }
}