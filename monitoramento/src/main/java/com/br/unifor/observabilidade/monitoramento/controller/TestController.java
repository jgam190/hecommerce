package com.br.unifor.observabilidade.monitoramento.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    private final Counter counter;

    public TestController(MeterRegistry meterRegistry){
        this.counter = Counter.builder("app_hello_requests_total")
                .description("Total de chamadas ao endpoint /hello")
                .tag("endpoint", "/hello")
                .register(meterRegistry);
    }

    @GetMapping("/hello")
    public Map<String, Object> hello(){
        counter.increment();

        Map<String,Object> response = new HashMap<>();
        response.put("message", "Olá : Aplicação Spring Boot com observabilidade!");
        response.put("timestamp", LocalDateTime.now());

        return response;
    }

    @GetMapping("/error-test")
    public Map<String, Object> errorTest(){
        throw new RuntimeException("Erro simulado para teste de observabilidade");
    }

}
