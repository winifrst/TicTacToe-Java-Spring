package org.tictactoe.web.filter;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tictactoe.domain.model.JwtAuthentication;
import org.tictactoe.domain.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        return path.startsWith("/auth/") ||
                path.equals("/refresh") ||
                path.contains("swagger") ||
                path.contains("swagger-ui") ||
                path.contains("api-docs") ||
                path.contains("webjars") ||
                path.contains("swagger-resources") ||
                path.contains("configuration/") ||
                path.equals("/swagger-ui.html");
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String token = getTokenFromRequest(request);
//
//        if (token != null) {
//            try {
//                JwtAuthentication authentication = authService.getAuthentication(token);
//
//                if (authentication != null && authentication.isAuthenticated()) {
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
//            } catch (Exception e) {
//                logger.error("Error processing JWT token", e);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        String path = request.getRequestURI();

        System.out.println("Processing request to: " + path);
        System.out.println("Token present: " + (token != null));

        if (token != null) {
            try {
                JwtAuthentication authentication = authService.getAuthentication(token);
                System.out.println("Authentication result: " + (authentication != null));

                if (authentication != null && authentication.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                logger.error("Error processing JWT token", e);
            }
        }

        filterChain.doFilter(request, response);
    }


    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}