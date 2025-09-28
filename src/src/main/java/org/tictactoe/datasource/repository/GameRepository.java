package org.tictactoe.datasource.repository;

import org.tictactoe.datasource.model.GameEntity;
import java.util.UUID;

public interface GameRepository {
    GameEntity save(GameEntity game);
    GameEntity findById(UUID id);
}