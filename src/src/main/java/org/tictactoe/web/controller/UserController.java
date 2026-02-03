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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.tictactoe.domain.model.JwtAuthentication;
import org.tictactoe.domain.service.StatisticsService;
import org.tictactoe.domain.service.UserService;
import org.tictactoe.web.model.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Tag(
        name = "Пользователи",
        description = "Операции с пользователями: получение информации и статистики"
)
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final StatisticsService statisticsService;

    public UserController(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    // endpoint для получения всех завершенных игр по accessToken, доступ к которому есть только у авторизованных пользователей.
    @GetMapping("/me/history")
    @Operation(
            summary = "Получить историю игр текущего пользователя",
            description = "Возвращает историю завершенных игр текущего пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "История игр получена",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GameHistoryResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<GameHistoryResponse>> getMyGameHistory() {
        try {
            UUID userId = getCurrentUserId();
            List<GameHistoryResponse> history = statisticsService.getFinishedGamesByUserId(userId);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated")) {
                return ResponseEntity.status(401).build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/me/stats")
    @Operation(
            summary = "Получить подробную статистику текущего пользователя",
            description = "Возвращает полную статистику игр текущего пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Статистика получена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PlayerStatsResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PlayerStatsResponse> getMyStats() {
        try {
            UUID userId = getCurrentUserId();
            PlayerStatsResponse stats = statisticsService.getPlayerStats(userId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not authenticated")) {
                return ResponseEntity.status(401).build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    private UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthentication jwtAuth) {
            return jwtAuth.getUserId();
        }
        throw new RuntimeException("User not authenticated");
    }

    @GetMapping("/{userId}/stats")
    @Operation(
            summary = "Получить статистику любого пользователя",
            description = """
            Возвращает полную статистику игр указанного пользователя.
            Доступно всем аутентифицированным пользователям.
            
            **Пример ответа:**
            ```json
            {
              "userId": "123e4567-e89b-12d3-a456-426614174000",
              "username": "player1",
              "totalGames": 15,
              "wins": 9,
              "losses": 4,
              "draws": 2,
              "winRate": 60.0
            }
            ```
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Статистика получена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PlayerStatsResponse.class),
                            examples = @ExampleObject(
                                    name = "Статистика пользователя",
                                    value = """
                        {
                          "userId": "123e4567-e89b-12d3-a456-426614174000",
                          "username": "player1",
                          "totalGames": 15,
                          "wins": 9,
                          "losses": 4,
                          "draws": 2,
                          "winRate": 60.0
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PlayerStatsResponse> getUserStats(
            @Parameter(
                    description = "UUID пользователя",
                    example = "123e4567-e89b-12d3-a456-426614174000"
            )
            @PathVariable UUID userId) {

        try {
            // Проверяем, существует ли пользователь
            if (!userService.findById(userId).isPresent()) {
                return ResponseEntity.notFound().build();
            }

            PlayerStatsResponse stats = statisticsService.getPlayerStats(userId);
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}