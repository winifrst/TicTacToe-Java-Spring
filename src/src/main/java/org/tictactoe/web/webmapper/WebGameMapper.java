package org.tictactoe.web.webmapper;

import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;
import org.tictactoe.web.model.GameRequest;
import org.tictactoe.web.model.GameResponse;

import java.util.UUID;

import static org.tictactoe.domain.service.Constants.BOARD_SIZE;

public class WebGameMapper {

    public static Game toDomainFromRequest(GameRequest request, UUID gameId) {
        Game game = new Game();
        game.setId(gameId);
        if (request != null && request.getBoard() != null) {
            game.setBoard(request.getBoard());
        }

        return game;
    }

    public static GameResponse toResponseFromDomain(Game game, GameStatus status) {
        GameResponse response = new GameResponse();

        if (game != null) {
            response.setGameId(game.getId() != null ? game.getId().toString() : "null");
            response.setBoard(game.getBoard() != null ? game.getBoard() : new int[BOARD_SIZE][BOARD_SIZE]);
        } else {
            response.setGameId("no-game");
            response.setBoard(new int[BOARD_SIZE][BOARD_SIZE]);
        }

        response.setStatus(status != null ? status.toString() : "UNKNOWN");

        return response;
    }

    public static GameResponse toNewGameResponse(UUID gameId) {
        GameResponse response = new GameResponse();
        response.setGameId(gameId.toString());
        response.setBoard(new int[BOARD_SIZE][BOARD_SIZE]);
        response.setStatus("NEW_GAME");

        return response;
    }

    public static GameResponse toErrorResponse(String errorMessage) {
        GameResponse response = new GameResponse();
        response.setGameId("error");
        response.setBoard(new int[BOARD_SIZE][BOARD_SIZE]);
        response.setStatus("ERROR: " + errorMessage);

        return response;
    }
}