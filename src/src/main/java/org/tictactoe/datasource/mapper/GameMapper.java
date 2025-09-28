package org.tictactoe.datasource.mapper;

import org.tictactoe.datasource.model.GameEntity;
import org.tictactoe.domain.model.Game;

public class GameMapper {
    public static Game toDomain(GameEntity entity) {
        Game game = new Game();
        // установите поля из entity в game
        return game;
    }

    public static GameEntity toEntity(Game domain) {
        GameEntity entity = new GameEntity();
        // установите поля из domain в entity
        return entity;
    }
}