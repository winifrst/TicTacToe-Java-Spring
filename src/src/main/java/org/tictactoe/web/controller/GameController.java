package org.tictactoe.web.controller;


import org.tictactoe.domain.model.Game;
import org.tictactoe.domain.service.GameService;
import org.tictactoe.web.model.GameRequest;
import org.tictactoe.web.model.GameResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/{gameId}")
    public GameResponse makeMove(@PathVariable UUID gameId, @RequestBody GameRequest request) {
        // Логика обработки хода
    }
}