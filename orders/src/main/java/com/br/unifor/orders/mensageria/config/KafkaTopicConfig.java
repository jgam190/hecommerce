package com.br.unifor.orders.mensageria.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuração de tópicos Kafka.
 */
@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic pedidoCriadoTopic() {
        return TopicBuilder.name("pedido-criado-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}