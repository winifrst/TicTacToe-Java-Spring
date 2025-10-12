//package org.tictactoe.web.controller;
//
//import org.tictactoe.datasource.mapper.GameMapper;
//import org.tictactoe.datasource.model.GameEntity;
//import org.tictactoe.datasource.repository.GameRepository;
//import org.tictactoe.domain.model.Game;
//import org.tictactoe.domain.model.GameStatus;
//import org.tictactoe.domain.service.GameService;
//import org.tictactoe.web.model.GameRequest;
//import org.tictactoe.web.model.GameResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/game")
//public class GameController {
//    private final GameService gameService;
//    private final GameRepository gameRepository;
//
//    // Добавляем GameRepository в конструктор
//    public GameController(GameService gameService, GameRepository gameRepository) {
//        this.gameService = gameService;
//        this.gameRepository = gameRepository;
//    }
//
//    @PostMapping("/{gameId}")
//    public ResponseEntity<GameResponse> makeMove(@PathVariable UUID gameId, @RequestBody GameRequest request) {
//        try {
//            // 1. Получаем игру из репозитория
//            GameEntity gameEntity = gameRepository.findById(gameId);
//            if (gameEntity == null) {
//                // Если игра не найдена, создаем новую
//                gameEntity = new GameEntity();
//                gameEntity.setId(gameId);
//                gameEntity.setBoard(new int[3][3]);
//                gameEntity.setPlayerTurn(true);
//            }
//
//            // 2. Преобразуем в domain-модель
//            Game game = GameMapper.toDomain(gameEntity);
//
//            // 3. Проверяем валидность хода
//            if (!gameService.validateBoard(game, request.getBoard())) {
//                GameResponse errorResponse = new GameResponse();
//                errorResponse.setStatus("INVALID_MOVE");
//                return ResponseEntity.badRequest().body(errorResponse);
//            }
//
//            // 4. Обновляем доску и проверяем статус
//            game.setBoard(request.getBoard());
//            game.setPlayerTurn(false);
//
//            GameStatus status = gameService.checkGameStatus(game);
//            if (status == GameStatus.IN_PROGRESS) {
//                // 5. Ход компьютера
//                game = gameService.makeComputerMove(game);
//                status = gameService.checkGameStatus(game);
//            }
//
//            // 6. Сохраняем обновленную игру
//            gameEntity = GameMapper.toEntity(game);
//            gameRepository.save(gameEntity);
//
//            // 7. Возвращаем ответ
//            GameResponse response = new GameResponse();
//            response.setGameId(gameId.toString());
//            response.setBoard(game.getBoard());
//            response.setStatus(status.toString());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            GameResponse errorResponse = new GameResponse();
//            errorResponse.setStatus("ERROR: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//    // Добавим метод для создания новой игры
//    @PostMapping("/new")
//    public ResponseEntity<GameResponse> createNewGame() {
//        UUID gameId = UUID.randomUUID();
//        GameEntity newGame = new GameEntity();
//        newGame.setId(gameId);
//        newGame.setBoard(new int[3][3]);
//        newGame.setPlayerTurn(true);
//
//        gameRepository.save(newGame);
//
//        GameResponse response = new GameResponse();
//        response.setGameId(gameId.toString());
//        response.setBoard(new int[3][3]);
//        response.setStatus("NEW_GAME");
//
//        return ResponseEntity.ok(response);
//    }
//}

package org.tictactoe.web.controller;

import org.tictactoe.datasource.mapper.GameMapper;
import org.tictactoe.datasource.model.GameEntity;
import org.tictactoe.datasource.repository.GameRepository;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;
import org.tictactoe.domain.service.GameService;
import org.tictactoe.web.model.GameRequest;
import org.tictactoe.web.model.GameResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final GameRepository gameRepository;

    public GameController(GameService gameService, GameRepository gameRepository) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<GameResponse> makeMove(@PathVariable UUID gameId, @RequestBody GameRequest request) {
        try {
            // 1. Получаем или создаём игру
            GameEntity gameEntity = gameRepository.findById(gameId);
            Game game;

            if (gameEntity == null) {
                // Новая игра
                game = new Game();
                game.setId(gameId);
//                game = new Game();
//                game.setId(gameId);
//                game.setBoard(new int[3][3]);  // ← ЯВНО установить пустое поле!
//                game.setPlayerTurn(true);      // ← Игрок ходит первым
            } else {
                // Существующая игра
                game = GameMapper.toDomain(gameEntity);
            }

            // 2. ВАЛИДАЦИЯ: проверяем корректность хода
            if (!gameService.validateBoard(game, request.getBoard())) {
                GameResponse errorResponse = new GameResponse();
                errorResponse.setStatus("INVALID_MOVE");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 3. Обновляем доску
            game.setBoard(request.getBoard());

            // 4. Проверяем статус и делаем ход компьютера
            GameStatus status = gameService.checkGameStatus(game);
            if (status == GameStatus.IN_PROGRESS) {
                game = gameService.makeComputerMove(game);
                status = gameService.checkGameStatus(game);
            }

            // 5. Сохраняем игру
            gameEntity = GameMapper.toEntity(game);
            gameRepository.save(gameEntity);

            // 6. Возвращаем ответ
            GameResponse response = new GameResponse();
            response.setGameId(gameId.toString());
            response.setBoard(game.getBoard());
            response.setStatus(status.toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            GameResponse errorResponse = new GameResponse();
            errorResponse.setStatus("ERROR: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

//    // Упрощенная валидация
//    private boolean isValidMove(int[][] board) {
//        int moves = 0;
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if (board[i][j] != 0) {
//                    moves++;
//                    if (board[i][j] != 1 && board[i][j] != 2) {
//                        return false; // Только 1 или 2 разрешены
//                    }
//                }
//            }
//        }
//        return moves >= 1; // Хотя бы один ход
//    }

    @PostMapping("/new")
    public ResponseEntity<GameResponse> createNewGame() {
        UUID gameId = UUID.randomUUID();

        // Создаем новую игру
        GameEntity newGame = new GameEntity();
        newGame.setId(gameId);
        newGame.setBoard(new int[3][3]);
        gameRepository.save(newGame);

        GameResponse response = new GameResponse();
        response.setGameId(gameId.toString());
        response.setBoard(new int[3][3]);
        response.setStatus("NEW_GAME");

        return ResponseEntity.ok(response);
    }
}