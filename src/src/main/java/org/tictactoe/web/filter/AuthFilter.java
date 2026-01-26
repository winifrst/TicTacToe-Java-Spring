//package org.tictactoe.web.filter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//import org.tictactoe.domain.service.AuthService;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class AuthFilter extends GenericFilterBean {
//    private final AuthService authService;
//
//    public AuthFilter(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        // "Разреши доступ без авторизации к endpoint'ам регистрации и авторизации."
//        String path = httpRequest.getRequestURI();
//
//        if (path.equals("/auth/signup") || path.equals("/auth/login")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Проверяем авторизацию
//        String authHeader = httpRequest.getHeader("Authorization");
//        UUID userId = authService.authenticate(authHeader);
//
//        if (userId != null) {
//
//            // 1. Создаём Authentication
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            userId,           // principal (кто)
//                            null,             // credentials
//                            List.of()          // authorities (не требуются по ТЗ)
//                    );
//
//            // 2. Кладём его в SecurityContext
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            // 3. (опционально, но можно оставить)
//            httpRequest.setAttribute("userId", userId);
//
//            chain.doFilter(request, response);
//        } else {
//            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            httpResponse.getWriter().write("Unauthorized");
//        }
//    }
//}
package org.tictactoe.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.tictactoe.domain.service.AuthService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class AuthFilter extends GenericFilterBean {

    private final AuthService authService;

    // Список путей, которые не требуют аутентификации
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/auth/**",
            "/swagger-ui",
            "/swagger-ui/",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/api-docs",
            "/api-docs/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/configuration/**"
    );

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Проверяем, является ли путь публичным
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Проверяем авторизацию для защищенных путей
        String authHeader = httpRequest.getHeader("Authorization");
        UUID userId = authService.authenticate(authHeader);

        if (userId != null) {
            // КРИТИЧНО: Создаем объект Authentication и помещаем его в SecurityContextHolder
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId.toString(), // principal (можно передать сам userId или UserDetails)
                            null,              // credentials
                            List.of(new SimpleGrantedAuthority("ROLE_USER")) // authorities
                    );

            // Устанавливаем аутентификацию в контекст безопасности
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Также сохраняем userId в атрибуты запроса для использования в контроллерах
            httpRequest.setAttribute("userId", userId);

            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Authentication required\"}");
        }
    }

    private boolean isPublicPath(String path) {
        // Проверяем точное совпадение
        if (PUBLIC_PATHS.contains(path)) {
            return true;
        }

        // Проверяем совпадение по паттерну
        for (String publicPath : PUBLIC_PATHS) {
            if (publicPath.endsWith("/**")) {
                String prefix = publicPath.substring(0, publicPath.length() - 3);
                if (path.startsWith(prefix)) {
                    return true;
                }
            }
        }

        return false;
    }
}