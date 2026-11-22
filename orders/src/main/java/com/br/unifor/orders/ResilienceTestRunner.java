package com.br.unifor.orders;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.web.client.RestClient;

import java.time.Duration;

public class ResilienceTestRunner {

    public static void main(String[] args) throws InterruptedException {

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .slidingWindowSize(5)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();

        CircuitBreaker circuitBreaker = CircuitBreaker.of("productService", config);


        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:9999")
                .build();



        for (int i = 1; i <= 20; i++) {
            int tentativa = i;
            long inicio = System.currentTimeMillis();

            try {

                String resposta = CircuitBreaker.decorateSupplier(circuitBreaker, () ->
                        restClient.get()
                                .uri("/products/1")
                                .retrieve()
                                .body(String.class)
                ).get();

                System.out.printf("[%02d/12]  SUCESSO: %s\n", tentativa, resposta);

            } catch (Exception e) {
                long duracao = System.currentTimeMillis() - inicio;
                String estadoCircuito = circuitBreaker.getState().name();

                if ("OPEN".equals(estadoCircuito)) {
                    System.out.printf("[%02d/12] ⚡ CIRCUITO ABERTO (%s) - Curto-circuito em %d ms!\n",
                            tentativa, estadoCircuito, duracao);
                } else {
                    System.out.printf("[%02d/12]  FALHA DE REDE (%s) - Erro em %d ms\n",
                            tentativa, estadoCircuito, duracao);
                }
            }

            Thread.sleep(400);
        }


    }
}