package org.tictactoe.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tictactoe.datasource.mapper.GameMapper;
import org.tictactoe.datasource.model.GameEntity;
import org.tictactoe.datasource.repository.GameRepository;
import org.tictactoe.datasource.repository.UserRepository;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;
import org.tictactoe.domain.service.GameService;
import org.tictactoe.web.model.GameRequest;
import org.tictactoe.web.model.GameResponse;
import org.tictactoe.web.model.MoveRequest;
import org.tictactoe.web.webmapper.WebGameMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//@RestController
//@RequestMapping("/game")
//public class GameController {
//

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public GameController(GameService gameService,
                          GameRepository gameRepository,
                          UserRepository userRepository) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<GameResponse> updateGame(
            @PathVariable UUID gameId,
            @RequestBody GameRequest request,
            HttpServletRequest httpRequest) {

        try {
            // 1. Проверяем авторизацию
            UUID userId = (UUID) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            // 2. Получаем игру
            Optional<GameEntity> gameEntityOptional = gameRepository.findById(gameId);
            if (gameEntityOptional.isEmpty()) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse("Game not found");
                return ResponseEntity.status(404).body(errorResponse);
            }

            Game game = GameMapper.toDomain(gameEntityOptional.get());

            // 3. Проверяем, что пользователь участвует в игре
            if (!gameService.isPlayerInGame(game, userId)) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse("You are not a player in this game");
                return ResponseEntity.status(403).body(errorResponse);
            }

            // 4. Валидируем новое состояние доски (проверяем, что изменилась только одна клетка)
            if (!gameService.validateBoard(game, request.getBoard())) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse(
                        "Invalid board state. Only one cell should be changed from previous move.");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 5. Находим, какой ход был сделан
            int[] newMove = findNewMove(game.getBoard(), request.getBoard());
            if (newMove[0] == -1) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse("No new move detected");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 6. Проверяем, что это ход текущего игрока
            if (!gameService.isPlayerTurn(game, userId)) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse("Not your turn");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 7. Проверяем валидность хода
            if (!gameService.validateMove(game, newMove[0], newMove[1], userId)) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse("Invalid move");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // 8. Обновляем игру с новым ходом
            game.setBoard(request.getBoard());

            // 9. Проверяем статус игры после хода пользователя
            game = gameService.checkGameStatus(game);

            // 10. Если игра с компьютером и не закончена - ход компьютера
            if (game.isAgainstComputer() &&
                    game.getStatus() != GameStatus.PLAYER_X_WON &&
                    game.getStatus() != GameStatus.PLAYER_O_WON &&
                    game.getStatus() != GameStatus.DRAW) {

                // Делаем ход компьютера (алгоритм Minimax)
                game = gameService.makeComputerMove(game);
                // Проверяем статус после хода компьютера
                game = gameService.checkGameStatus(game);
            }

            // 11. Сохраняем обновленную игру
            GameEntity updatedEntity = gameRepository.save(GameMapper.toEntity(game));
            Game updatedGame = GameMapper.toDomain(updatedEntity);

            // 12. Возвращаем ответ
            GameResponse response = WebGameMapper.toResponseFromDomain(updatedGame, userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            GameResponse errorResponse = WebGameMapper.toErrorResponse("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<GameResponse> makeSimpleMove(
            @PathVariable UUID gameId,
            @RequestBody MoveRequest moveRequest,
            HttpServletRequest request) {

        try {
            UUID userId = (UUID) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            Optional<GameEntity> gameEntityOpt = gameRepository.findById(gameId);
            if (gameEntityOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Game game = GameMapper.toDomain(gameEntityOpt.get());

            if (!gameService.validateMove(game, moveRequest.getRow(), moveRequest.getCol(), userId)) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse("Invalid move");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Создаем новую доску с ходом
            int[][] newBoard = copyBoard(game.getBoard());
            newBoard[moveRequest.getRow()][moveRequest.getCol()] = game.getPlayerSymbolCode(userId);

            // Обновляем игру
            game.setBoard(newBoard);
            game = gameService.checkGameStatus(game);

            if (game.isAgainstComputer() && game.getStatus() == GameStatus.COMPUTER_TURN) {
                game = gameService.makeComputerMove(game);
                game = gameService.checkGameStatus(game);
            }

            GameEntity updatedEntity = gameRepository.save(GameMapper.toEntity(game));
            Game updatedGame = GameMapper.toDomain(updatedEntity);

            GameResponse response = WebGameMapper.toResponseFromDomain(updatedGame, userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            GameResponse errorResponse = WebGameMapper.toErrorResponse(e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    private int[] findNewMove(int[][] oldBoard, int[][] newBoard) {
        for (int i = 0; i < oldBoard.length; i++) {
            for (int j = 0; j < oldBoard[i].length; j++) {
                if (oldBoard[i][j] != newBoard[i][j]) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, 3);
        }
        return newBoard;
    }

    @PostMapping("/new")
    public ResponseEntity<GameResponse> createGame(
            HttpServletRequest request,
            @RequestParam(defaultValue = "computer") String opponent,
            @RequestParam(required = false) String playerXSymbol,
            @RequestParam(required = false) String playerOSymbol) {

        try {
            UUID userId = (UUID) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            Game game = new Game();
            game.setPlayerXId(userId);
            game.setPlayerXSymbol(playerXSymbol != null ? playerXSymbol : "X");
            game.setPlayerOSymbol(playerOSymbol != null ? playerOSymbol : "O");

            if ("computer".equalsIgnoreCase(opponent)) {
                game.setAgainstComputer(true);
                game.setStatus(GameStatus.PLAYER_X_TURN);
                game.setCurrentPlayerId(userId);
            } else {
                game.setAgainstComputer(false);
                game.setStatus(GameStatus.WAITING_FOR_PLAYERS);
            }

            GameEntity savedEntity = gameRepository.save(GameMapper.toEntity(game));
            Game savedGame = GameMapper.toDomain(savedEntity);

            GameResponse response = WebGameMapper.toResponseFromDomain(savedGame, userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            GameResponse errorResponse = WebGameMapper.toErrorResponse("Failed to create game: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/available")
    public ResponseEntity<List<GameResponse>> getAvailableGames(HttpServletRequest request) {
        try {
            UUID userId = (UUID) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            List<Game> availableGames = StreamSupport.stream(gameRepository.findAll().spliterator(), false)
                    .map(GameMapper::toDomain)
                    .filter(game -> game.getStatus() == GameStatus.WAITING_FOR_PLAYERS)
                    .filter(game -> !game.isPlayerX(userId))
                    .collect(Collectors.toList());

            List<GameResponse> responses = availableGames.stream()
                    .map(game -> WebGameMapper.toResponseFromDomain(game, userId))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoint для присоединения к игре
    @PostMapping("/{gameId}/join")
    public ResponseEntity<GameResponse> joinGame(
            @PathVariable UUID gameId,
            HttpServletRequest request) {

        try {
            UUID userId = (UUID) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            Optional<GameEntity> gameEntityOpt = gameRepository.findById(gameId);
            if (gameEntityOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Game game = GameMapper.toDomain(gameEntityOpt.get());

            if (game.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse("Game is not waiting for players");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (game.isPlayerX(userId)) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse("You are already in this game");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            game.setPlayerOId(userId);
            game.setStatus(GameStatus.PLAYER_X_TURN);
            game.setCurrentPlayerId(game.getPlayerXId());

            GameEntity updatedEntity = gameRepository.save(GameMapper.toEntity(game));
            Game updatedGame = GameMapper.toDomain(updatedEntity);

            GameResponse response = WebGameMapper.toResponseFromDomain(updatedGame, userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            GameResponse errorResponse = WebGameMapper.toErrorResponse("Failed to join game: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}