package org.tictactoe.domain.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.tictactoe.datasource.repository.UserRepository;
import org.tictactoe.domain.model.JwtAuthentication;
import org.tictactoe.domain.model.User;
import org.tictactoe.web.model.JwtRequest;
import org.tictactoe.web.model.JwtResponse;
import org.tictactoe.web.model.RefreshJwtRequest;
import org.tictactoe.web.model.SignUpRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // Хранилище для отозванных токенов
        private final Map<String, LocalDateTime> revokedTokens = new ConcurrentHashMap<>();

    public AuthServiceImpl(UserService userService,
                           JwtProvider jwtProvider,
                           JwtUtil jwtUtil,
                           UserRepository userRepository) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public boolean register(SignUpRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return false;
        }
        userService.createUser(request.getUsername(), request.getPassword());
        return true;
    }

    @Override
    public JwtResponse login(JwtRequest request) {
        if (!userService.validateUser(request.getUsername(), request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        Optional<User> userOpt = userService.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public JwtResponse getNewAccessToken(String refreshToken) {
        // Проверяем, не отозван ли токен
        if (isTokenRevoked(refreshToken)) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        var claims = jwtProvider.getRefreshClaims(refreshToken);
        String userIdStr = claims.get("userId", String.class);
        UUID userId = UUID.fromString(userIdStr);

        // Проверяем, существует ли пользователь
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        String newAccessToken = jwtProvider.generateAccessToken(user);

        return new JwtResponse(newAccessToken, refreshToken);
    }

    @Override
    public JwtResponse getNewRefreshToken(String refreshToken) {
        // Проверяем, не отозван ли токен
        if (isTokenRevoked(refreshToken)) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        // Отзываем старый токен
        revokeToken(refreshToken);

        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        var claims = jwtProvider.getRefreshClaims(refreshToken);
        String userIdStr = claims.get("userId", String.class);
        UUID userId = UUID.fromString(userIdStr);

        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        return new JwtResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public JwtAuthentication getAuthentication(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (isTokenRevoked(token)) {
            return null;
        }

        if (!jwtProvider.validateAccessToken(token)) {
            return null;
        }

        try {
            var claims = jwtProvider.getAccessClaims(token);
            return jwtUtil.generateAuthentication(claims);
        } catch (Exception e) {
            return null;
        }
    }

    // Метод для отзыва токена
    public void revokeToken(String token) {
        revokedTokens.put(token, LocalDateTime.now());
    }

    // Метод для проверки отозван ли токен
    private boolean isTokenRevoked(String token) {
        return revokedTokens.containsKey(token);
    }

    // Метод для очистки старых отозванных токенов
    public void cleanupRevokedTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        revokedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
    }
}