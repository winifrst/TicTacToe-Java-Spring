package org.tictactoe.datasource.mapper;

import org.tictactoe.datasource.model.UserEntity;
import org.tictactoe.domain.model.User;
import org.tictactoe.domain.model.Role;

import java.util.stream.Collectors;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setPassword(entity.getPassword());

        if (entity.getRoles() != null) {
            user.setRoles(
                    entity.getRoles().stream()
                            .map(roleName -> Role.valueOf(roleName))
                            .collect(Collectors.toSet())
            );
        }

        return user;
    }

    public static UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());

        if (domain.getRoles() != null) {
            entity.setRoles(
                    domain.getRoles().stream()
                            .map(Role::name)
                            .collect(Collectors.toSet())
            );
        }

        return entity;
    }
}