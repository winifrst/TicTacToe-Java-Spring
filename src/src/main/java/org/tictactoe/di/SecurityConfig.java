//package org.tictactoe.di;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.tictactoe.web.filter.AuthFilter;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final AuthFilter authFilter;
//
//    public SecurityConfig(AuthFilter authFilter) {
//        this.authFilter = authFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
////                .authorizeHttpRequests(auth -> auth
////                        .anyRequest().permitAll()
////                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/signup", "/auth/login").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(authFilter, BasicAuthenticationFilter.class);  // "Воспользуйся AuthFilter в качестве фильтра."
//
//        return http.build();
//    }
//}

package org.tictactoe.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.tictactoe.web.filter.AuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthFilter authFilter;

    public SecurityConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Разрешаем доступ к Swagger UI и OpenAPI документации
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/api-docs.yaml",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/configuration/**"
                        ).permitAll()

                        // Разрешаем доступ к эндпоинтам аутентификации
                        .requestMatchers("/auth/signup", "/auth/login").permitAll()

                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authFilter, BasicAuthenticationFilter.class);

        return http.build();
    }
}