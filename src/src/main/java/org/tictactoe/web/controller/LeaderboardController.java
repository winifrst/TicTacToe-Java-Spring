package org.tictactoe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tictactoe.domain.service.StatisticsService;
import org.tictactoe.web.model.PlayerStatsResponse;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@Tag(
        name = "Таблица лидеров",
        description = "Операции с таблицей лидеров"
)
@SecurityRequirement(name = "bearerAuth")
public class LeaderboardController {

    private final StatisticsService statisticsService;

    public LeaderboardController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // Добавь endpoint для получения первых N лучших игроков
    @GetMapping("/top")
    @Operation(
            summary = "Получить топ N игроков",
            description = "Возвращает список лучших игроков, отсортированных по проценту побед"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список лучших игроков",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PlayerStatsResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Некорректный параметр limit"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<PlayerStatsResponse>> getTopPlayers(
            @Parameter(
                    description = "Количество игроков в топе (максимум 100, по умолчанию 10)",
                    example = "10"
            )
            @RequestParam(defaultValue = "10") int limit) {

        if (limit <= 0 || limit > 100) {
            return ResponseEntity.badRequest().build();
        }

        List<PlayerStatsResponse> topPlayers = statisticsService.getTopPlayers(limit);
        return ResponseEntity.ok(topPlayers);
    }
}