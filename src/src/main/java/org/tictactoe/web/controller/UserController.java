// org.tictactoe.web.controller.UserController.java
package org.tictactoe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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

    @GetMapping("/{userId}")
    @Operation(
            summary = "Получить информацию о пользователе по ID",
            description = "Возвращает основную информацию о пользователе с его статистикой"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Информация о пользователе",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable UUID userId) {
        try {
            return userService.findById(userId)
                    .map(user -> {
                        UserResponse response = new UserResponse();
                        response.setId(user.getId());
                        response.setUsername(user.getUsername());

                        // Получаем статистику пользователя из StatisticsService
                        PlayerStatsResponse stats = statisticsService.getPlayerStats(userId);
                        response.setGamesPlayed(stats.getTotalGames());
                        response.setWins(stats.getWins());
                        response.setWinRate(stats.getWinRate());

                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/me")
    @Operation(
            summary = "Получение информации о текущем пользователе",
            description = "Возвращает информацию о текущем пользователе с его статистикой"
    )
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuth) {
            UUID userId = jwtAuth.getUserId();
            return userService.findById(userId)
                    .map(user -> {
                        UserResponse response = new UserResponse();
                        response.setId(user.getId());
                        response.setUsername(user.getUsername());

                        // Получаем статистику пользователя из StatisticsService
                        PlayerStatsResponse stats = statisticsService.getPlayerStats(userId);
                        response.setGamesPlayed(stats.getTotalGames());
                        response.setWins(stats.getWins());
                        response.setWinRate(stats.getWinRate());

                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(401).build();
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
}