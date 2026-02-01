package org.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import org.tictactoe.datasource.mapper.UserMapper;
import org.tictactoe.datasource.model.UserEntity;
import org.tictactoe.datasource.repository.UserRepository;
import org.tictactoe.domain.model.User;
import org.tictactoe.domain.model.Role;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username, String password) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword(password);

        user.addRole(Role.ROLE_USER);

        userRepository.save(UserMapper.toEntity(user));
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean validateUser(String username, String password) {

        try {
            // Получаем пользователя из БД
            Optional<UserEntity> entityOpt = userRepository.findByUsername(username);
            if (entityOpt.isEmpty()) {
                return false;
            }
            UserEntity entity = entityOpt.get();
            // Конвертируем в domain
            User user = UserMapper.toDomain(entity);
            // Сравниваем пароли
            boolean passwordMatches = password.equals(user.getPassword());
            return passwordMatches;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}