package com.br.unifor.notifications.mensageria;

import com.br.unifor.notifications.mensageria.dto.NotificationModel;
import com.br.unifor.notifications.mensageria.dto.PedidoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final List<NotificationModel> notificacoesEmMemoria = new CopyOnWriteArrayList<>();

    public void processarNotificacao(PedidoEvent evento) {
        log.info("[NOTIFICAÇÃO] Processando evento do Pedido ID: {}", evento.orderId());

        NotificationModel novaNotificacao = new NotificationModel(
                UUID.randomUUID().toString(),
                evento.orderId(),
                evento.clienteId(),
                evento.status(),
                evento.valorTotal() != null ? evento.valorTotal().doubleValue() : 0.0
        );

        notificacoesEmMemoria.add(0, novaNotificacao); // Adiciona no início da lista (mais recentes primeiro)
    }

    public List<NotificationModel> obterTodasNotificacoes() {
        return notificacoesEmMemoria;
    }

    public List<NotificationModel> obterPorCliente(String clienteId) {
        return notificacoesEmMemoria.stream()
                .filter(n -> n.getClienteId() != null && n.getClienteId().equalsIgnoreCase(clienteId))
                .collect(Collectors.toList());
    }

    public void marcarComoLida(String id) {
        notificacoesEmMemoria.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .ifPresent(n -> n.setLida(true));
    }
}