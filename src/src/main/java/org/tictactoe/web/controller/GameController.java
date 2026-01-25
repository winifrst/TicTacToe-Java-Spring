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
import org.tictactoe.web.webmapper.WebGameMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
            UUID userId = (UUID) httpRequest.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            Optional<GameEntity> gameEntityOptional = gameRepository.findById(gameId);
            if (gameEntityOptional.isEmpty()) {
                return ResponseEntity.status(404).build();
            }

            Game game = GameMapper.toDomain(gameEntityOptional.get());


            printBoard(game.getBoard());
            printBoard(request.getBoard());

            if (!gameService.isPlayerInGame(game, userId)) {
                return ResponseEntity.status(403).build();
            }

            boolean isValid = gameService.validateBoard(game, request.getBoard(), userId);

            if (!isValid) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse("Invalid move");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            int[] newMove = findNewMove(game.getBoard(), request.getBoard());

            if (newMove[0] == -1) {
                GameResponse errorResponse = WebGameMapper.toErrorResponse("No new move detected");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            game = gameService.makeMove(game, newMove[0], newMove[1], userId);


            // Проверяем статус игры
            game = gameService.checkGameStatus(game);

            // Если игра с компьютером
            if (game.isAgainstComputer() &&
                    game.getStatus() != GameStatus.PLAYER_X_WON &&
                    game.getStatus() != GameStatus.PLAYER_O_WON &&
                    game.getStatus() != GameStatus.DRAW) {

                game = gameService.makeComputerMove(game);
                game = gameService.checkGameStatus(game);
            }

            GameEntity updatedEntity = gameRepository.save(GameMapper.toEntity(game));
            Game updatedGame = GameMapper.toDomain(updatedEntity);

            GameResponse response = WebGameMapper.toResponseFromDomain(updatedGame, userId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            GameResponse errorResponse = WebGameMapper.toErrorResponse("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
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

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> getGame(
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

            if (!gameService.isPlayerInGame(game, userId)) {
                return ResponseEntity.status(403).build();
            }

            GameResponse response = WebGameMapper.toResponseFromDomain(game, userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            GameResponse errorResponse = WebGameMapper.toErrorResponse("Failed to get game: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
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