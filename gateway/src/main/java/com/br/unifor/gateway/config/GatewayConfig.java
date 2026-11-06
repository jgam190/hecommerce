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
            @Value("${services.products.base-url}") String productsBaseUrl,
            @Value("${services.orders.base-url}") String ordersBaseUrl,
            @Value("${services.auth.base-url}") String authBaseUrl) {
        return builder.routes()
                // Serviço de Autenticação (stripPrefix(2): /api/auth/** -> /login)
                .route(r -> r.path("/api/auth/**")
                        .filters(f -> f.stripPrefix(2))
                        .uri(authBaseUrl))
                .route(r -> r.path("/api/protected/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(authBaseUrl))

                // Serviço de Catálogo (Produtos)
                .route(r -> r.path("/api/products/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(productsBaseUrl))

                // Serviço de Pedidos (stripPrefix(1): /api/orders/** -> /orders)
                .route(r -> r.path("/api/orders/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(ordersBaseUrl))

                // Serviço de Pagamentos
                .route(r -> r.path("/api/payments/**")
                        .uri("http://payments:8080"))

                // Serviço de Logística
                .route(r -> r.path("/api/logistics/**")
                        .uri("http://logistics:8080"))

                // Serviço de Recomendação
                .route(r -> r.path("/api/recommendation/**")
                        .uri("http://recommendation:8080"))

                .build();
    }
}
