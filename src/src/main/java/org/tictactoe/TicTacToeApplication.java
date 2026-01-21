package org.tictactoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TicTacToeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicTacToeApplication.class, args);

        System.out.println("""          
                 \u001B[1;32m
                ************************
                * Tic Tac Toe started! *
                ************************
                \u001B[0m""");
    }
}