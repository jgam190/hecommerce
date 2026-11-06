package com.br.unifor.orders.mensageria;

import com.br.unifor.orders.domain.OrderEventPublisher;
import com.br.unifor.orders.domain.model.Order;
import com.br.unifor.orders.mensageria.dto.PedidoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Implementação do OrderEventPublisher utilizando Kafka para publicar eventos de pedido.
 */
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaOrderEventPublisher.class);
    private static final String TOPICO_PEDIDO_CRIADO = "pedido-criado-topic";

    private final KafkaTemplate<String, PedidoEvent> kafkaTemplate;

    public KafkaOrderEventPublisher(KafkaTemplate<String, PedidoEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishOrderCreated(Order order) {
        PedidoEvent evento = new PedidoEvent(
                order.getId().toString(),
                order.getUserId(),
                order.getStatus().toString(),
                order.getTotal()
        );
        kafkaTemplate.send(TOPICO_PEDIDO_CRIADO, evento.orderId(), evento);
        logger.info("Evento de pedido publicado no Kafka: {}", evento.orderId());
    }
}