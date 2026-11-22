package com.br.unifor.orders.infrastructure.client;

import com.br.unifor.orders.domain.ProductClient;
import com.br.unifor.orders.domain.model.ProdutoDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalTime;

@Component
public class ProductRestClient implements ProductClient {

    private static final Logger log = LoggerFactory.getLogger(ProductRestClient.class);

    private final RestTemplate restTemplate;
    private final String productsBaseUrl;

    public ProductRestClient(RestTemplate restTemplate, String productsBaseUrl) {
        this.restTemplate = restTemplate;
        this.productsBaseUrl = productsBaseUrl;
    }

    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "findProductByIdFallback")
    @Retry(name = "productService")
    public ProdutoDto findProductById(String productId) {

        log.info("[RETRY] Tentando conectar ao serviço de produtos para o ID: {} às {}",
                productId, LocalTime.now());

        String url = productsBaseUrl + "/products/" + productId;
        return restTemplate.getForObject(url, ProdutoDto.class);
    }

    public ProdutoDto findProductByIdFallback(String productId, Throwable throwable) {

        log.error("Falha ao consultar produto ID: {} via HTTP. Motivo: {}",
                productId, throwable.getMessage());

        ProdutoDto fallbackProduct = new ProdutoDto();
        fallbackProduct.setId(productId);
        fallbackProduct.setNome("Produto indisponível temporariamente");
        fallbackProduct.setPreco(BigDecimal.valueOf(0.0));

        return fallbackProduct;
    }
}