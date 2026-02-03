package org.tictactoe.domain.service;

import org.tictactoe.domain.model.JwtAuthentication;
import org.tictactoe.web.model.JwtRequest;
import org.tictactoe.web.model.JwtResponse;
import org.tictactoe.web.model.RefreshJwtRequest;
import org.tictactoe.web.model.SignUpRequest;

import java.util.Optional;
import java.util.UUID;

public interface AuthService {
    // Регистрация
    boolean register(SignUpRequest request);

    // Аутентификация с JWT
    JwtResponse login(JwtRequest request);

    // Обновление Access Token
    JwtResponse getNewAccessToken(String refreshToken);

    // Обновление Refresh Token
    JwtResponse getNewRefreshToken(String refreshToken);

    // Получение аутентификации (для фильтра)
    JwtAuthentication getAuthentication(String token);
}