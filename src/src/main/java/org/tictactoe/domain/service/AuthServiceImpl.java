package org.tictactoe.domain.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.tictactoe.domain.model.JwtAuthentication;
import org.tictactoe.domain.model.User;
import org.tictactoe.web.model.JwtRequest;
import org.tictactoe.web.model.JwtResponse;
import org.tictactoe.web.model.RefreshJwtRequest;
import org.tictactoe.web.model.SignUpRequest;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserService userService,
                           JwtProvider jwtProvider,
                           JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.jwtUtil = jwtUtil;
    }

    // Регистрация (остаётся без изменений)
    @Override
    public boolean register(SignUpRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return false;
        }
        userService.createUser(request.getUsername(), request.getPassword());
        return true;
    }

    // Аутентификация с JWT
    @Override
    public JwtResponse login(JwtRequest request) {
        // Проверяем пользователя
        if (!userService.validateUser(request.getUsername(), request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Находим пользователя
        Optional<User> userOpt = userService.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Генерируем токены
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new JwtResponse(accessToken, refreshToken);
    }

    // Обновление Access Token
    @Override
    public JwtResponse getNewAccessToken(String refreshToken) {
        // Валидируем refresh token
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Получаем claims
        var claims = jwtProvider.getRefreshClaims(refreshToken);
        String userIdStr = claims.get("userId", String.class);
        UUID userId = UUID.fromString(userIdStr);

        // Находим пользователя
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Генерируем новый access token
        String newAccessToken = jwtProvider.generateAccessToken(user);

        // Возвращаем старый refresh token (по ТЗ refresh token остаётся тем же)
        return new JwtResponse(newAccessToken, refreshToken);
    }

    // Обновление Refresh Token
    @Override
    public JwtResponse getNewRefreshToken(String refreshToken) {
        // Валидируем старый refresh token
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Получаем claims
        var claims = jwtProvider.getRefreshClaims(refreshToken);
        String userIdStr = claims.get("userId", String.class);
        UUID userId = UUID.fromString(userIdStr);

        // Находим пользователя
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // Генерируем новые токены
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        return new JwtResponse(newAccessToken, newRefreshToken);
    }

    // Получение аутентификации (для фильтра)
    @Override
    public JwtAuthentication getAuthentication(String token) {
        // Убираем префикс "Bearer " если есть
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Валидируем access token
        if (!jwtProvider.validateAccessToken(token)) {
            return null;
        }

        // Получаем claims
        var claims = jwtProvider.getAccessClaims(token);

        // Создаём аутентификацию
        return jwtUtil.generateAuthentication(claims);
    }

    // Метод для получения текущего пользователя (удобно)
    public Optional<UUID> getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UUID) {
            return Optional.of((UUID) authentication.getPrincipal());
        }
        return Optional.empty();
    }
}