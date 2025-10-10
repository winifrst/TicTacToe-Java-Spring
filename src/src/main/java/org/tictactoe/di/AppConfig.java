package org.tictactoe.di;

import org.tictactoe.datasource.repository.GameRepository;
import org.tictactoe.datasource.repository.InMemoryGameRepository;
import org.tictactoe.domain.service.GameService;
import org.tictactoe.domain.service.GameServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

//    @Bean
//    public GameRepository gameRepository() {
//        return new InMemoryGameRepository();
//    }

    @Bean
    public GameService gameService(GameRepository repository) {
        return new GameServiceImpl(repository);  // теперь конструктор правильный
    }
}