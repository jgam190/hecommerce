package com.br.unifor.orders.infrastructure.client;

import com.br.unifor.orders.domain.ProductClient;
import com.br.unifor.orders.domain.model.ProdutoDto;
import org.springframework.web.client.RestTemplate;

public class ProductRestClient implements ProductClient {

    private final RestTemplate restTemplate;
    private final String productsBaseUrl;

    public ProductRestClient(RestTemplate restTemplate, String productsBaseUrl) {
        this.restTemplate = restTemplate;
        this.productsBaseUrl = productsBaseUrl;
    }

    @Override
    public ProdutoDto findProductById(String productId) {
        String url = productsBaseUrl + "/products/" + productId;
        return restTemplate.getForObject(url, ProdutoDto.class);
    }
}
