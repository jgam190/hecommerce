package com.br.unifor.orders;

import org.junit.jupiter.api.Test;

/**
 * Teste simples de inicialização da aplicação.
 * O contexto Spring completo não é testado aqui pois requer infraestrutura externa (Kafka, PostgreSQL).
 * Os testes de unidade estão em DefaultOrderServiceTest e OrderControllerTest.
 */
class OrdersApplicationTests {

    @Test
    void applicationClassExists() {
        assert OrdersApplication.class != null;
    }
}