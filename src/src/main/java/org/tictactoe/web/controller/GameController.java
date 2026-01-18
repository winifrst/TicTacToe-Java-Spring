package org.tictactoe.web.controller;

import org.tictactoe.datasource.mapper.GameMapper;
import org.tictactoe.datasource.model.GameEntity;
import org.tictactoe.datasource.repository.GameRepository;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;
import org.tictactoe.domain.service.GameService;
import org.tictactoe.web.webmapper.WebGameMapper;
import org.tictactoe.web.model.GameRequest;
import org.tictactoe.web.model.GameResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static org.tictactoe.domain.service.Constants.BOARD_SIZE;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final GameRepository gameRepository;
//    private GameEntity newGame;

    public GameController(GameService gameService, GameRepository gameRepository) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<GameResponse> makeMove(@PathVariable UUID gameId, @RequestBody GameRequest request) {
        try {
            Optional<GameEntity> gameEntityOptional = gameRepository.findById(gameId);  // Получаем или создаём игру
            Game game;

//            if (gameEntity == null) {
//                game = WebGameMapper.toDomainFromRequest(request, gameId);
//            } else {
//                game = GameMapper.toDomain(gameEntity);
//            }

            if (gameEntityOptional.isEmpty()) {
                game = new Game();
                game.setId(gameId);
                game.setBoard(new int[BOARD_SIZE][BOARD_SIZE]);
                game.setPlayerTurn(true);
            } else {
                GameEntity gameEntity = gameEntityOptional.get();
                game = GameMapper.toDomain(gameEntity);
            }


            if (!gameService.validateBoard(game, request.getBoard())) {  // проверяем корректность хода
                GameResponse errorResponse = WebGameMapper.toErrorResponse("INVALID_MOVE");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            game.setBoard(request.getBoard());  // обновляем доску

            GameStatus status = gameService.checkGameStatus(game);  // проверяем статус и делаем ход компьютера
            if (status == GameStatus.IN_PROGRESS) {
                game = gameService.makeComputerMove(game);
                status = gameService.checkGameStatus(game);
            }

            GameEntity gameEntity = GameMapper.toEntity(game);  // сохраняем игру
            gameRepository.save(gameEntity);

            GameResponse response = WebGameMapper.toResponseFromDomain(game, status);  // используем уродский маппер

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            GameResponse errorResponse = WebGameMapper.toErrorResponse(e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<GameResponse> createNewGame() {
        UUID gameId = UUID.randomUUID();

        GameEntity newGame = new GameEntity();
        newGame.setId(gameId);
        newGame.setBoard("0,0,0,0,0,0,0,0,0");
        gameRepository.save(newGame);

        GameResponse response = WebGameMapper.toNewGameResponse(gameId);  // используем уродский маппер

        return ResponseEntity.ok(response);
    }
}