package com.br.unifor.notifications.mensageria;

import com.br.unifor.notifications.mensageria.dto.PedidoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void processarNotificacao(PedidoEvent evento) {
        log.info("[NOTIFICAÇÃO] Processando evento do Pedido ID: {}", evento.orderId());
        log.info(" - Cliente: {}", evento.clienteId());
        log.info(" - Status: {}", evento.status());
        log.info(" - Valor Total: R$ {}", evento.valorTotal());
    }
}