package org.tictactoe.domain.service;

import org.tictactoe.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findById(UUID id);  // Явное указание на возможное отсутствие значения

    Optional<User> findByUsername(String username);

    User createUser(String username, String password);

    boolean validateUser(String username, String password);

    boolean existsByUsername(String username);
}
