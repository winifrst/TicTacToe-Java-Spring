package org.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import org.tictactoe.datasource.mapper.GameMapper;
import org.tictactoe.datasource.mapper.UserMapper;
import org.tictactoe.datasource.repository.GameRepository;
import org.tictactoe.datasource.repository.UserRepository;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.User;
import org.tictactoe.web.model.GameHistoryResponse;
import org.tictactoe.web.model.PlayerStatsResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public StatisticsService(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    // для работы с играми метод для получения всех завершенных игр по UUID пользователя
    public List<GameHistoryResponse> getFinishedGamesByUserId(UUID userId) {
        var gameEntities = gameRepository.findFinishedGamesByUserId(userId);

        return gameEntities.stream()
                .map(GameMapper::toDomain)
                .map(game -> toGameHistoryResponse(game, userId))
                .sorted(Comparator.comparing(GameHistoryResponse::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public PlayerStatsResponse getPlayerStats(UUID userId) {
        int totalGames = gameRepository.countFinishedGamesByUserId(userId);
        int wins = gameRepository.countWinsByUserId(userId);
        int losses = gameRepository.countLossesByUserId(userId);
        int draws = gameRepository.countDrawsByUserId(userId);

        Optional<User> userOpt = userRepository.findById(userId)
                .map(UserMapper::toDomain);

        PlayerStatsResponse stats = new PlayerStatsResponse();
        stats.setUserId(userId);
        userOpt.ifPresent(user -> stats.setUsername(user.getUsername()));
        stats.setTotalGames(totalGames);
        stats.setWins(wins);
        stats.setLosses(losses);
        stats.setDraws(draws);
        stats.setWinRate(totalGames > 0 ? (double) wins / totalGames * 100 : 0);

        return stats;
    }

    public List<PlayerStatsResponse> getTopPlayers(int limit) {
        List<Object[]> results = gameRepository.findTopPlayers(limit);
        List<PlayerStatsResponse> topPlayers = new ArrayList<>();

        for (Object[] row : results) {
            UUID userId = (UUID) row[0];
            String username = (String) row[1];
            Long wins = ((Number) row[2]).longValue();
            Long losses = ((Number) row[3]).longValue();
            Long draws = ((Number) row[4]).longValue();
            long totalGames = wins + losses + draws;

            PlayerStatsResponse stats = new PlayerStatsResponse();
            stats.setUserId(userId);
            stats.setUsername(username);
            stats.setWins(wins.intValue());
            stats.setLosses(losses.intValue());
            stats.setDraws(draws.intValue());
            stats.setTotalGames((int) totalGames);
            stats.setWinRate(totalGames > 0 ? (double) wins / totalGames * 100 : 0);

            topPlayers.add(stats);
        }

        return topPlayers;
    }

    private GameHistoryResponse toGameHistoryResponse(Game game, UUID currentUserId) {
        GameHistoryResponse response = new GameHistoryResponse();
        response.setGameId(game.getId());
        response.setStatus(game.getStatus().toString());
        response.setCreatedAt(game.getCreatedAt());
        response.setAgainstComputer(game.isAgainstComputer());

        // Определяем символ текущего пользователя
        if (game.isPlayerX(currentUserId)) {
            response.setPlayerSymbol(game.getPlayerXSymbol());
            response.setOpponentSymbol(game.getPlayerOSymbol());
            response.setOpponentId(game.getPlayerOId());
        } else {
            response.setPlayerSymbol(game.getPlayerOSymbol());
            response.setOpponentSymbol(game.getPlayerXSymbol());
            response.setOpponentId(game.getPlayerXId());
        }

        // Определяем результат для текущего пользователя
        if (game.getStatus() == org.tictactoe.domain.model.GameStatus.PLAYER_X_WON) {
            if (game.isPlayerX(currentUserId)) {
                response.setResult("WIN");
            } else {
                response.setResult("LOSS");
            }
        } else if (game.getStatus() == org.tictactoe.domain.model.GameStatus.PLAYER_O_WON) {
            if (game.isPlayerO(currentUserId)) {
                response.setResult("WIN");
            } else {
                response.setResult("LOSS");
            }
        } else {
            response.setResult("DRAW");
        }

        // Получаем имя противника
        if (response.getOpponentId() != null && !game.isAgainstComputer()) {
            userRepository.findById(response.getOpponentId())
                    .map(UserMapper::toDomain)
                    .ifPresent(user -> response.setOpponentUsername(user.getUsername()));
        } else if (game.isAgainstComputer()) {
            response.setOpponentUsername("Computer");
        }

        return response;
    }
}