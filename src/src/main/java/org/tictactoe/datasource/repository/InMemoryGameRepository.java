package org.tictactoe.datasource.repository;

import org.springframework.stereotype.Repository;
import org.tictactoe.datasource.model.GameEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {
    private final Map<UUID, GameEntity> storage = new ConcurrentHashMap<>();

    @Override
    public GameEntity save(GameEntity game) {
        storage.put(game.getId(), game);
        return game;
    }

    @Override
    public GameEntity findById(UUID id) {
        return storage.get(id);
    }
}