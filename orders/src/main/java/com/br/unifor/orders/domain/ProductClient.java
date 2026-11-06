package com.br.unifor.orders.domain;

import com.br.unifor.orders.domain.model.ProdutoDto;

/**
 * Porta de acesso ao serviço de Produtos (catálogo).
 */
public interface ProductClient {
    ProdutoDto findProductById(String productId);
}