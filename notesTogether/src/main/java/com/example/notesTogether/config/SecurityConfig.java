package com.example.notesTogether.config;

import com.example.notesTogether.filters.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Enables Spring Security filter chain integration
public class SecurityConfig {
    // Custom JWT filter that validates JWT on each request
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Defines the main Spring Security filter chain.
     * This controls:
     *  - which routes require authentication
     *  - which authentication mechanisms are enabled
     *  - session behavior
     *  - custom filters (JWT)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF protection (safe because we use stateless JWTs, not sessions)
                .csrf(AbstractHttpConfigurer::disable)

                // Authorization rules for HTTP requests
                .authorizeHttpRequests(request -> request
                        // Swagger / OpenAPI (must be fully open)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Public auth endpoints
                        .requestMatchers(
                                "/api/users/login"
                        ).permitAll()

                        // Everything else requires JWT
                        .anyRequest().authenticated()
                )

                // Configure session management
                .sessionManagement(
                        session ->
                                // Do not create or use HTTP sessions
                                // Each request must be authenticated independently (JWT)
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Register JWT filter BEFORE username/password authentication filter
                // This ensures JWT is processed first on every request
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Build the security filter chain
                .build();
    }
}
