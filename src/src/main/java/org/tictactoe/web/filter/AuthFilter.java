package org.tictactoe.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.tictactoe.domain.service.AuthService;

import java.io.IOException;
import java.util.UUID;

@Component
public class AuthFilter extends GenericFilterBean {
    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Пропускаем публичные endpoints
        String path = httpRequest.getRequestURI();
        if (path.startsWith("/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        // Проверяем авторизацию
        String authHeader = httpRequest.getHeader("Authorization");
        UUID userId = authService.authenticate(authHeader);

        if (userId != null) {
            // Добавляем userId в атрибуты запроса для дальнейшего использования
            httpRequest.setAttribute("userId", userId);
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Unauthorized");
        }
    }
}