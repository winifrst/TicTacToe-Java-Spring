package org.tictactoe.datasource.mapper;

import org.tictactoe.datasource.model.GameEntity;
import org.tictactoe.domain.model.Game;

import java.util.UUID;

public class GameMapper {
    public static Game toDomain(GameEntity entity) {
        Game game = new Game();
        game.setId(entity.getId());
        game.setBoard(entity.getBoard());
        game.setPlayerTurn(entity.isPlayerTurn());
        return game;
    }

    public static GameEntity toEntity(Game domain) {
        GameEntity entity = new GameEntity();
        entity.setId(domain.getId());
        entity.setBoard(domain.getBoard());
        entity.setPlayerTurn(domain.isPlayerTurn());
        return entity;
    }
}