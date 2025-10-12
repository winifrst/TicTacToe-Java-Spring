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

    //    @Override
//    public int[] getNextMove(Game game) {
//        int[][] board = game.getBoard();
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (board[i][j] == 0) {
//                    return new int[]{i, j};
//                }
//            }
//        }
//        return new int[]{-1, -1};
//    }
//    @Override
//    public int[] getNextMove(Game game) {
//        // TODO: Заменить на Minimal алгоритм
//        // Вместо поиска первой пустой клетки
//        // Нужно анализировать лучший ход
//
//        return findBestMove(game.getBoard());
//    }

//    private int[] findBestMove(int[][] board) {
//        // Здесь будет сложная логика Minimal
//        // Анализ всех возможных ходов
//        // Выбор оптимального
//        return new int[]{0, 0};
//    }

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
//        if (!game.isPlayerTurn()) {
//            return false;
//        }

        // проверяем, что сделан ровно один ход
        int movesMade = 0;

        int[][] oldBoard = game.getBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (movesMade > 1) {
                    return false;
                }

                if (oldBoard[i][j] != newBoard[i][j]) {
                    movesMade++;
                    if (oldBoard[i][j] != 0 || newBoard[i][j] != 1) {
                        return false;
                    }
                }
            }
        }


        return (movesMade == 1);
    }

    public GameStatus checkGameStatus(Game game) {
        return checkBoardStatus(game.getBoard());
    }

    private GameStatus checkBoardStatus(int[][] board) {
        // Проверка строк
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != 0 && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0] == 1 ? GameStatus.PLAYER_WON : GameStatus.COMPUTER_WON;
            }
        }

        // Проверка столбцов
        for (int j = 0; j < 3; j++) {
            if (board[0][j] != 0 && board[0][j] == board[1][j] && board[1][j] == board[2][j]) {
                return board[0][j] == 1 ? GameStatus.PLAYER_WON : GameStatus.COMPUTER_WON;
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
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return GameStatus.IN_PROGRESS;
                }
            }
        }

        return GameStatus.DRAW;
    }

    private int minimax(int[][] board, int depth, boolean isMaximizing) {
        GameStatus status = checkBoardStatus(board);

        // Базовые случаи рекурсии
        if (status == GameStatus.COMPUTER_WON) return 10 - depth;  // Компьютер выиграл
        if (status == GameStatus.PLAYER_WON) return depth - 10;    // Игрок выиграл
        if (status == GameStatus.DRAW) return 0;                   // Ничья

        if (isMaximizing) {
            // Ход компьютера (максимизируем оценку)
            int bestScore = Integer.MIN_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == 0) {  // Пустая клетка
                        board[i][j] = 2;     // Компьютер ходит (2)
                        int score = minimax(board, depth + 1, false);  // Рекурсия!
                        board[i][j] = 0;     // Отмена хода

                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;

        } else {
            // Ход игрока (минимизируем оценку)
            int bestScore = Integer.MAX_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == 0) {  // Пустая клетка
                        board[i][j] = 1;     // Игрок ходит (1)
                        int score = minimax(board, depth + 1, true);   // Рекурсия!
                        board[i][j] = 0;     // Отмена хода

                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    @Override
    public int[] getNextMove(Game game) {
        int[][] board = copyBoard(game.getBoard());
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        // Перебираем все возможные ходы
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    // Пробуем ход
                    board[i][j] = 2;  // Компьютер

                    // Оцениваем этот ход рекурсивно
                    int score = minimax(board, 0, false);

                    // Отменяем ход
                    board[i][j] = 0;

                    // Находим лучший ход
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
    public Game makeComputerMove(Game game) {

        int[] move = getNextMove(game);  // minimax move

        if (move[0] != -1) {
            // Копируем доску
            int[][] newBoard = copyBoard(game.getBoard());

            // Ставим ход компьютера
            newBoard[move[0]][move[1]] = 2;

            // 4. Обновляем игру
            game.setBoard(newBoard);
            game.setPlayerTurn(true);  // Ход переходит к игроку

            System.out.println("Компьютер походил: [" + move[0] + ", " + move[1] + "]");
        } else {
            System.out.println("Нет возможных ходов для компьютера");
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