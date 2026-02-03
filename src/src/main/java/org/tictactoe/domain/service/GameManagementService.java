package org.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import org.tictactoe.datasource.mapper.GameMapper;
import org.tictactoe.datasource.model.GameEntity;
import org.tictactoe.datasource.repository.GameRepository;
import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;
import org.tictactoe.web.model.GameResponse;
import org.tictactoe.web.webmapper.WebGameMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GameManagementService {

    private final GameRepository gameRepository;
    private final GameService gameService;

    public GameManagementService(GameRepository gameRepository,
                                 GameService gameService) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }

    public Optional<Game> findGameById(UUID gameId) {
        return gameRepository.findById(gameId)
                .map(GameMapper::toDomain);
    }

    public Game saveGame(Game game) {
        GameEntity entity = GameMapper.toEntity(game);
        GameEntity savedEntity = gameRepository.save(entity);
        return GameMapper.toDomain(savedEntity);
    }

    public List<Game> getAvailableGames(UUID currentUserId) {
        return StreamSupport.stream(gameRepository.findAll().spliterator(), false)
                .map(GameMapper::toDomain)
                .filter(game -> game.getStatus() == GameStatus.WAITING_FOR_PLAYERS)
                .filter(game -> !gameService.isPlayerInGame(game, currentUserId))
                .collect(Collectors.toList());
    }

    public boolean isPlayerInGame(Game game, UUID playerId) {
        return gameService.isPlayerInGame(game, playerId);
    }

    public boolean isPlayerInGameById(UUID gameId, UUID playerId) {
        Optional<Game> gameOpt = findGameById(gameId);
        return gameOpt.map(game -> gameService.isPlayerInGame(game, playerId))
                .orElse(false);
    }

    public GameResponse convertToResponse(Game game, UUID currentUserId) {
        return WebGameMapper.toResponseFromDomain(game, currentUserId);
    }
}