package org.tictactoe.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Ответ с информацией о пользователе")
public class UserResponse {
    @Schema(description = "UUID пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Логин пользователя", example = "player1")
    private String username;

    @Schema(description = "Количество сыгранных игр", example = "10")
    private int gamesPlayed;

    @Schema(description = "Количество побед", example = "7")
    private int wins;

    @Schema(description = "Количество поражений", example = "2")
    private int losses;

    @Schema(description = "Количество ничьих", example = "1")
    private int draws;

    @Schema(description = "Процент побед", example = "70.0")
    private double winRate;

    @Schema(description = "Позиция в таблице лидеров", example = "5", nullable = true)
    private Integer leaderboardPosition;

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }

    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }

    public Integer getLeaderboardPosition() { return leaderboardPosition; }
    public void setLeaderboardPosition(Integer leaderboardPosition) {
        this.leaderboardPosition = leaderboardPosition;
    }
}