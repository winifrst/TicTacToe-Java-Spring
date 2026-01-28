package org.tictactoe.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tictactoe.domain.model.JwtAuthentication;
import org.tictactoe.domain.service.AuthService;

import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Публичные эндпоинты (без авторизации)
        return path.startsWith("/auth/") ||
                path.contains("swagger") ||
                path.contains("api-docs") ||
                path.contains("webjars") ||
                path.contains("swagger-resources") ||
                path.contains("configuration/") ||
                path.equals("/swagger-ui.html") ||
                path.equals("/refresh");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("AuthFilter processing: " + path + " | Method: " + request.getMethod());

        // Получаем токен из заголовка Authorization
        String token = getTokenFromRequest(request);

        if (token != null) {
            try {
                // Получаем аутентификацию из токена
                JwtAuthentication authentication = authService.getAuthentication(token);

                if (authentication != null && authentication.isAuthenticated()) {
                    // Устанавливаем аутентификацию в SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("User authenticated: " + authentication.getUserId());
                } else {
                    System.out.println("Invalid or expired token");
                }
            } catch (Exception e) {
                System.err.println("Error processing JWT token: " + e.getMessage());
                // Не прерываем цепочку - пусть другие фильтры/контроллеры обработают
            }
        } else {
            System.out.println("No Authorization header found");
        }

        filterChain.doFilter(request, response);

        // Очищаем SecurityContext после запроса
        SecurityContextHolder.clearContext();
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Убираем "Bearer "
        }

        return null;
    }
}