// UserServiceImpl.java
package org.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import org.tictactoe.datasource.mapper.UserMapper;
import org.tictactoe.datasource.model.UserEntity;
import org.tictactoe.datasource.repository.UserRepository;
import org.tictactoe.domain.model.User;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
//        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public User createUser(String username, String password) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword(password);

        userRepository.save(UserMapper.toEntity(user));
        return user;
    }

//    @Override
//    public Optional<User> findById(UUID id) {
//        if (userRepository.findById(id).isPresent()) {
//            return Optional.of(UserMapper.toDomain(userRepository.findById(id).get()));
//        } else {
//            return Optional.empty();
//        }
//    }

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

    //    @Override
//    public boolean validateUser(String username, String password) {
//        Optional<User> userOpt = findByUsername(username);
//        if (userOpt.isEmpty()) {
//            return false;
//        }
//
//        User user = userOpt.get();
//        return password.equals(user.getPassword());
//    }
    @Override
    public boolean validateUser(String username, String password) {
        System.out.println("=== VALIDATE USER START ===");
        System.out.println("Looking for username: " + username);

        try {
            // Получаем пользователя из БД
            Optional<UserEntity> entityOpt = userRepository.findByUsername(username);
            System.out.println("User found in DB? " + entityOpt.isPresent());

            if (entityOpt.isEmpty()) {
                System.out.println("User not found in database");
                return false;
            }

            UserEntity entity = entityOpt.get();
            System.out.println("Entity from DB - ID: " + entity.getId());
            System.out.println("Entity from DB - Username: " + entity.getUsername());
            System.out.println("Entity from DB - Password: " + entity.getPassword());

            // Конвертируем в domain
            User user = UserMapper.toDomain(entity);
            System.out.println("Domain user - Username: " + user.getUsername());
            System.out.println("Domain user - Password: " + user.getPassword());

            // Сравниваем пароли
            boolean passwordMatches = password.equals(user.getPassword());
            System.out.println("Password matches? " + passwordMatches);
            System.out.println("Input password: '" + password + "'");
            System.out.println("DB password: '" + user.getPassword() + "'");

            return passwordMatches;

        } catch (Exception e) {
            System.err.println("ERROR in validateUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}