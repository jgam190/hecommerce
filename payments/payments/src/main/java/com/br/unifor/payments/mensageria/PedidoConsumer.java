package com.br.unifor.payments.mensageria;

import com.br.unifor.payments.mensageria.dto.PedidoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PedidoConsumer {
    private static final Logger logger = LoggerFactory.getLogger(PedidoConsumer.class);

    @KafkaListener(
            topics = "pedido-criado-topic",
            groupId = "payments-group-v3",
            properties = {
                    "value.deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
                    "spring.json.trusted.packages=*",
                    "spring.json.use.type.headers=false",
                    "spring.json.value.default.type=com.br.unifor.payments.mensageria.dto.PedidoEvent"
            }
    )
    public void consumir(PedidoEvent evento) {
        logger.info("Iniciando processamento de pagamento para o pedido recebido: {}", evento.orderId());
        logger.info("Valor a cobrar: R$ {}", evento.valorTotal());
    }
}