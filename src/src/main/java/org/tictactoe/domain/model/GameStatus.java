package org.tictactoe.domain.model;

public enum GameStatus {
    WAITING_FOR_PLAYERS,
    PLAYER_X_TURN,
    PLAYER_O_TURN,
    PLAYER_X_WON,
    PLAYER_O_WON,
    DRAW,
    COMPUTER_TURN
}
