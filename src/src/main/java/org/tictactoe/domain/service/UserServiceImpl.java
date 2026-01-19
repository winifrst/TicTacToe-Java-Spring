// UserServiceImpl.java
package org.tictactoe.domain.service;

import org.springframework.stereotype.Service;
import org.tictactoe.datasource.mapper.UserMapper;
import org.tictactoe.datasource.repository.UserRepository;
import org.tictactoe.domain.model.User;
import org.tictactoe.domain.service.UserService;

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

//    public Optional<User> findByUsername(String username) {
//        if (userRepository.findByUsername(username).isPresent()) {
//            return Optional.of(UserMapper.toDomain(userRepository.findByUsername(username).get()));
//        } else {
//            return Optional.empty();
//        }
//    }

    @Override
    public boolean validateUser(String username, String password) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return password.equals(user.getPassword());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}