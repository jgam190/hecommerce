package com.br.unifor.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/products/**", "/products/**").permitAll()
                        .pathMatchers("/api/auth/**", "/auth/**").permitAll()
                        .pathMatchers("/api/orders/**", "/orders/**").permitAll()
                        .pathMatchers("/api/notifications/**", "/notifications/**").permitAll() // <- Liberado para evitar o 401
                        .anyExchange().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}