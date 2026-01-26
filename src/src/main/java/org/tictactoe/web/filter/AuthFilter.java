package org.tictactoe.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.tictactoe.domain.service.AuthService;

import java.io.IOException;
import java.util.List;
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

        // "Разреши доступ без авторизации к endpoint'ам регистрации и авторизации."
        String path = httpRequest.getRequestURI();

        if (path.equals("/auth/signup") || path.equals("/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        // Проверяем авторизацию
        String authHeader = httpRequest.getHeader("Authorization");
        UUID userId = authService.authenticate(authHeader);

//        if (userId != null) {
//            // Добавляем userId в атрибуты запроса для дальнейшего использования
//            httpRequest.setAttribute("userId", userId);
//            chain.doFilter(request, response);
        if (userId != null) {

            // 1. Создаём Authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,           // principal (кто)
                            null,             // credentials
                            List.of()          // authorities (не требуются по ТЗ)
                    );

            // 2. Кладём его в SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. (опционально, но можно оставить)
            httpRequest.setAttribute("userId", userId);

            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Unauthorized");
        }
    }
}