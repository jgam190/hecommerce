package com.br.unifor.orders.configuration;

import com.br.unifor.orders.domain.OrderEventPublisher;
import com.br.unifor.orders.domain.ProductClient;
import com.br.unifor.orders.infrastructure.client.ProductRestClient;
import com.br.unifor.orders.mensageria.KafkaOrderEventPublisher;
import com.br.unifor.orders.mensageria.dto.PedidoEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ClientConfiguration {

    @Bean
    @ConditionalOnMissingBean(KafkaAdmin.class)
    public KafkaAdmin kafkaAdmin(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return new KafkaAdmin(Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
    }

    @Bean
    @Primary
    public OrderEventPublisher orderEventPublisherKafka(KafkaTemplate<String, PedidoEvent> kafkaTemplate) {
        return new KafkaOrderEventPublisher(kafkaTemplate);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, PedidoEvent> kafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ProductClient productRestClient(
            RestTemplate restTemplate,
            @Value("${services.products.base-url}") String productsBaseUrl) {
        return new ProductRestClient(restTemplate, productsBaseUrl);
    }
}
