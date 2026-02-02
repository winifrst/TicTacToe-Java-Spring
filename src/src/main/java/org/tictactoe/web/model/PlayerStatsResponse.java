// org.tictactoe.web.model.PlayerStatsResponse.java
package org.tictactoe.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Статистика игрока")
public class PlayerStatsResponse {
    @Schema(description = "UUID пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "Имя пользователя", example = "player1")
    private String username;

    @Schema(description = "Всего сыграно игр", example = "10")
    private int totalGames;

    @Schema(description = "Выиграно игр", example = "6")
    private int wins;

    @Schema(description = "Проиграно игр", example = "3")
    private int losses;

    @Schema(description = "Ничьих", example = "1")
    private int draws;

    @Schema(description = "Процент побед", example = "60.0")
    private double winRate;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getTotalGames() { return totalGames; }
    public void setTotalGames(int totalGames) { this.totalGames = totalGames; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }

    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }
}