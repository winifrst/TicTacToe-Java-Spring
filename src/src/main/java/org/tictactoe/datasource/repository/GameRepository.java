package org.tictactoe.datasource.repository;

import org.springframework.data.repository.CrudRepository;
import org.tictactoe.datasource.model.GameEntity;

import java.util.UUID;

public interface GameRepository extends CrudRepository<GameEntity, UUID> {
//    GameEntity save(GameEntity game);
//
//    GameEntity findById(UUID id);
}