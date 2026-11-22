package com.br.unifor.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração das rotas do API Gateway.
 * Cada serviço tem sua própria rota e filtro de path.
 */
@Configuration
public class GatewayConfig {

    /**
     * Define as rotas do gateway com prefixos de API.
     */
    @Bean
    public RouteLocator apiRoutes(
            RouteLocatorBuilder builder,
            @Value("${services.auth.base-url}") String authBaseUrl) {
        return builder.routes()
                .route(r -> r.path("/api/protected/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(authBaseUrl))
                .route(r -> r.path("/api/payments/**")
                        .uri("http://payments:8080"))
                .route(r -> r.path("/api/logistics/**")
                        .uri("http://logistics:8080"))
                .route(r -> r.path("/api/recommendation/**")
                        .uri("http://recommendation:8080"))

                .build();
    }
}
