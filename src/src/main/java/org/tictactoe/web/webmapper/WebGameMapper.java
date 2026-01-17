package org.tictactoe.web.webmapper;

//package org.tictactoe.web.mapper;

import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;
import org.tictactoe.web.model.GameResponse;
import org.tictactoe.web.model.GameRequest;

import java.util.UUID;

public class WebGameMapper {

    // Уродский метод 1: Прямое копирование без проверок
    public static Game toDomainFromRequest(GameRequest request, UUID gameId) {
        Game game = new Game();
        game.setId(gameId);
        if (request != null && request.getBoard() != null) {
            game.setBoard(request.getBoard());
        }
        // Игнорируем все остальные поля из request, если бы они были
        return game;
    }

    // Уродский метод 2: Статический без DI
    public static GameResponse toResponseFromDomain(Game game, GameStatus status) {
        GameResponse response = new GameResponse();

        // Прямой сет полей без проверок
        if (game != null) {
            response.setGameId(game.getId() != null ? game.getId().toString() : "null");
            response.setBoard(game.getBoard() != null ? game.getBoard() : new int[3][3]);
        } else {
            response.setGameId("no-game");
            response.setBoard(new int[3][3]);
        }

        // Просто строковое представление
        response.setStatus(status != null ? status.toString() : "UNKNOWN");

        return response;
    }

    // Уродский метод 3: Для новой игры
    public static GameResponse toNewGameResponse(UUID gameId) {
        GameResponse response = new GameResponse();
        response.setGameId(gameId.toString());
        response.setBoard(new int[3][3]);
        response.setStatus("NEW_GAME");
        return response;
    }

    // Уродский метод 4: Для ошибки
    public static GameResponse toErrorResponse(String errorMessage) {
        GameResponse response = new GameResponse();
        response.setGameId("error");
        response.setBoard(new int[3][3]);
        response.setStatus("ERROR: " + errorMessage);
        return response;
    }
}