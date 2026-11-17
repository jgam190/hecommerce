package com.br.unifor.notifications.mensageria;

import com.br.unifor.notifications.mensageria.dto.PedidoEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final NotificationService notificationService;

    public NotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${app.kafka.topic.pedido:pedido-criado-topic}",
            groupId = "notification-group"
    )
    public void consumirEventoPedido(PedidoEvent evento) {
        notificationService.processarNotificacao(evento);
    }
}