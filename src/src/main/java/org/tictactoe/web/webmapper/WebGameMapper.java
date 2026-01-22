package org.tictactoe.web.webmapper;

import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.model.GameStatus;
import org.tictactoe.web.model.GameResponse;

import java.util.UUID;

import static org.tictactoe.domain.service.Constants.BOARD_SIZE;

public class WebGameMapper {

    public static GameResponse toResponseFromDomain(Game game, UUID currentUserId) {
        GameResponse response = new GameResponse();

        response.setGameId(game.getId().toString());
        response.setBoard(game.getBoard());
        response.setStatus(game.getStatus().toString());

        response.setPlayerXId(game.getPlayerXId());
        response.setPlayerOId(game.getPlayerOId());
        response.setCurrentPlayerId(game.getCurrentPlayerId());
        response.setAgainstComputer(game.isAgainstComputer());
        response.setCreatedAt(game.getCreatedAt());

        response.setPlayerXSymbol(game.getPlayerXSymbol());
        response.setPlayerOSymbol(game.getPlayerOSymbol());

        // Определяем символ текущего пользователя
        if (currentUserId != null) {
            if (game.isPlayerX(currentUserId)) {
                response.setPlayerSymbol(game.getPlayerXSymbol());  // X
            } else if (game.isPlayerO(currentUserId)) {
                response.setPlayerSymbol(game.getPlayerOSymbol());  // O
            } else {
                // Пользователь не в игре
                response.setPlayerSymbol(null);
            }
        } else {
            response.setPlayerSymbol(null);
        }

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