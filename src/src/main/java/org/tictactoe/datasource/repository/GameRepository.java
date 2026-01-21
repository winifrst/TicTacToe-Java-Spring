package org.tictactoe.datasource.repository;

import org.springframework.data.repository.CrudRepository;
import org.tictactoe.datasource.model.GameEntity;
import org.tictactoe.domain.model.GameStatus;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameEntity, UUID> {
    List<GameEntity> findByStatus(GameStatus status);
    List<GameEntity> findByPlayerXIdOrPlayerOId(UUID playerXId, UUID playerOId);
}