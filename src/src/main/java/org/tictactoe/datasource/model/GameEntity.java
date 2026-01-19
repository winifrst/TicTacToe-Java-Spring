package org.tictactoe.datasource.model;

//import jakarta.persistence.Entity;
//import org.jetbrains.kotlin.com.google.common.collect.Table;

import jakarta.persistence.*;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    private UUID id;

    @Column(name = "board")
    private String board;

    @Column(name = "player_turn")
    private boolean isPlayer1Turn;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private UserEntity user;


    public GameEntity() {
    }

    public GameEntity(UUID id, String board, boolean isPlayerTurn) {
        this.id = id;
        this.board = board;
        this.isPlayer1Turn = isPlayerTurn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBoard() {
        return board;
    }

    public UserEntity getUser() { return user; }

    public void setBoard(String board) {
        this.board = board;
    }

    public boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        isPlayer1Turn = playerTurn;
    }

    public void setUser(UserEntity user) { this.user = user; }
}