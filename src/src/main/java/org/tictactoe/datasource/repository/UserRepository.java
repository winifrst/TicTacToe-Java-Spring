package org.tictactoe.datasource.repository;

import org.springframework.data.repository.CrudRepository;
import org.tictactoe.datasource.model.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
