package org.tictactoe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;
import org.tictactoe.domain.service.GameService;
import org.tictactoe.domain.service.GameManagementService;
import org.tictactoe.web.model.GameRequest;
import org.tictactoe.web.model.GameResponse;
import org.tictactoe.web.webmapper.WebGameMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/game")
@Tag(
        name = "Игры",
        description = "Управление игровыми сессиями"
)
@SecurityRequirement(name = "bearerAuth")
public class GameController {
    private final GameService gameService;
//    private final GameRepository gameRepository;
    private final GameManagementService gameManagementService;


    public GameController(GameService gameService,
                          GameManagementService gameManagementService) {
        this.gameService = gameService;
        this.gameManagementService = gameManagementService;
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UUID) {
            return (UUID) auth.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }

    @PostMapping("/{gameId}")
    @Operation(
            summary = "Сделать ход в игре",
            description = "Выполняет ход в указанной игре. После хода пользователя, " +
                    "если игра против компьютера, компьютер делает ответный ход автоматически."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ход успешно выполнен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameResponse.class),
                            examples = @ExampleObject(
                                    name = "Успешный ход",
                                    value = """
                    {
                        "gameId": "123e4567-e89b-12d3-a456-426614174000",
                        "board": [[0,0,1],[0,2,0],[0,0,0]],
                        "status": "PLAYER_O_TURN",
                        "playerXId": "123e4567-e89b-12d3-a456-426614174000",
                        "playerOId": "223e4567-e89b-12d3-a456-426614174001",
                        "currentPlayerId": "223e4567-e89b-12d3-a456-426614174001",
                        "againstComputer": false,
                        "playerSymbol": "X",
                        "createdAt": "2024-01-15T10:30:00",
                        "playerXSymbol": "X",
                        "playerOSymbol": "O"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректный ход или данные",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameResponse.class),
                            examples = @ExampleObject(
                                    name = "Ошибка хода",
                                    value = "{\"gameId\": \"error\", \"status\": \"ERROR: Invalid move\"}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к игре"),
            @ApiResponse(responseCode = "404", description = "Игра не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GameResponse> updateGame(
            @PathVariable UUID gameId,
            @RequestBody GameRequest request) {

        try {
            UUID userId = getCurrentUserId();

            Optional<Game> gameOpt = gameManagementService.findGameById(gameId);
            if (gameOpt.isEmpty()) {
                return ResponseEntity.status(404).build();
            }

            Game game = gameOpt.get();

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

            game = gameService.checkGameStatus(game);

            if (game.isAgainstComputer() &&
                    game.getStatus() != GameStatus.PLAYER_X_WON &&
                    game.getStatus() != GameStatus.PLAYER_O_WON &&
                    game.getStatus() != GameStatus.DRAW) {

                game = gameService.makeComputerMove(game);
                game = gameService.checkGameStatus(game);
            }

            Game updatedGame = gameManagementService.saveGame(game);
            GameResponse response = gameManagementService.convertToResponse(updatedGame, userId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated")) {
                return ResponseEntity.status(401).build();
            }
            e.printStackTrace();
            GameResponse errorResponse = WebGameMapper.toErrorResponse("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/new")
    @Operation(
            summary = "Создать новую игру",
            description = "Создает новую игровую сессию. Можно выбрать игру против компьютера или ожидание второго игрока."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Игра успешно создана",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "400", description = "Ошибка при создании игры")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GameResponse> createGame(
            @Parameter(
                    description = """
            Тип противника:
            - **computer** - игра против ИИ
            - **human** - игра против другого игрока (ожидание подключения)
            """,
                    required = false,
                    schema = @Schema(
                            allowableValues = {"computer", "human"},
                            defaultValue = "computer",
                            example = "computer"
                    ),
                    examples = {
                            @ExampleObject(
                                    name = "Против компьютера",
                                    value = "computer",
                                    description = "Игра против искусственного интеллекта"
                            ),
                            @ExampleObject(
                                    name = "Против человека",
                                    value = "human",
                                    description = "Игра против другого игрока (мультиплеер)"
                            )
                    }
            )
            @RequestParam(defaultValue = "computer") String opponent) {

        try {
            UUID userId = getCurrentUserId();

            Game game = new Game();
            game.setPlayerXId(userId);

            if ("computer".equalsIgnoreCase(opponent)) {
                game.setAgainstComputer(true);
                game.setStatus(GameStatus.PLAYER_X_TURN);
                game.setCurrentPlayerId(userId);
            } else {
                game.setAgainstComputer(false);
                game.setStatus(GameStatus.WAITING_FOR_PLAYERS);
            }

            Game savedGame = gameManagementService.saveGame(game);
            GameResponse response = gameManagementService.convertToResponse(savedGame, userId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated")) {
                return ResponseEntity.status(401).build();
            }
            GameResponse errorResponse = WebGameMapper.toErrorResponse("Failed to create game: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/available")
    @Operation(
            summary = "Получить список доступных игр",
            description = "Возвращает список игр, ожидающих второго игрока. " +
                    "Не включает игры, где текущий пользователь уже является участником."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список доступных игр",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameResponse[].class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<GameResponse>> getAvailableGames() {
        try {
            UUID userId = getCurrentUserId();
            List<Game> availableGames = gameManagementService.getAvailableGames(userId);
            List<GameResponse> responses = availableGames.stream()
                    .map(game -> gameManagementService.convertToResponse(game, userId))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated")) {
                return ResponseEntity.status(401).build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{gameId}/join")
    @Operation(
            summary = "Присоединиться к игре",
            description = "Текущий пользователь присоединяется к игре в качестве второго игрока (игрок O). " +
                    "Доступно только для игр со статусом WAITING_FOR_PLAYERS."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешно присоединились к игре",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Невозможно присоединиться к игре"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Игра не найдена")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GameResponse> joinGame(@PathVariable UUID gameId) {
        try {
            UUID userId = getCurrentUserId();
            Optional<Game> gameOpt = gameManagementService.findGameById(gameId);
            if (gameOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Game game = gameOpt.get();

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

            Game updatedGame = gameManagementService.saveGame(game);
            GameResponse response = gameManagementService.convertToResponse(updatedGame, userId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated")) {
                return ResponseEntity.status(401).build();
            }
            GameResponse errorResponse = WebGameMapper.toErrorResponse("Failed to join game: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{gameId}")
    @Operation(
            summary = "Получить информацию об игре",
            description = "Возвращает текущее состояние указанной игры. " +
                    "Доступно только для участников игры."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Информация об игре",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Нет доступа к игре"),
            @ApiResponse(responseCode = "404", description = "Игра не найдена")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GameResponse> getGame(@PathVariable UUID gameId) {
        try {
            UUID userId = getCurrentUserId();

            Optional<Game> gameOpt = gameManagementService.findGameById(gameId);
            if (gameOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Game game = gameOpt.get();

            if (!gameService.isPlayerInGame(game, userId)) {
                return ResponseEntity.status(403).build();
            }

            GameResponse response = gameManagementService.convertToResponse(game, userId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated")) {
                return ResponseEntity.status(401).build();
            }
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