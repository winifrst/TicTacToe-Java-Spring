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

import static org.tictactoe.domain.service.Constants.BOARD_SIZE;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final GameRepository gameRepository;
    private GameEntity newGame;

    public GameController(GameService gameService, GameRepository gameRepository) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<GameResponse> makeMove(@PathVariable UUID gameId, @RequestBody GameRequest request) {
        try {
            GameEntity gameEntity = gameRepository.findById(gameId);  // Получаем или создаём игру
            Game game;

            if (gameEntity == null) {
                game = new Game();
                game.setId(gameId);
            } else {
                game = GameMapper.toDomain(gameEntity);
            }

            if (!gameService.validateBoard(game, request.getBoard())) {  // проверяем корректность хода
                GameResponse errorResponse = new GameResponse();
                errorResponse.setStatus("INVALID_MOVE");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            game.setBoard(request.getBoard());  // обновляем доску

            GameStatus status = gameService.checkGameStatus(game);  // проверяем статус и делаем ход компьютера
            if (status == GameStatus.IN_PROGRESS) {
                game = gameService.makeComputerMove(game);
                status = gameService.checkGameStatus(game);
            }

            gameEntity = GameMapper.toEntity(game);  // сохраняем игру
            gameRepository.save(gameEntity);

            GameResponse response = new GameResponse();  // возвращаем ответ
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

    @PostMapping("/new")
    public ResponseEntity<GameResponse> createNewGame() {
        UUID gameId = UUID.randomUUID();

        GameEntity newGame = new GameEntity();
        newGame.setId(gameId);
        newGame.setBoard(new int[BOARD_SIZE][BOARD_SIZE]);
        gameRepository.save(newGame);

        GameResponse response = new GameResponse();
        response.setGameId(gameId.toString());
        response.setBoard(new int[BOARD_SIZE][BOARD_SIZE]);
        response.setStatus("NEW_GAME");

        return ResponseEntity.ok(response);
    }
}