package com.br.unifor.orders.mensageria.dto;

import java.math.BigDecimal;

/**
 * Evento de pedido publicado no Kafka.
 */
public record PedidoEvent(
        String orderId,
        String clienteId,
        String status,
        BigDecimal valorTotal
) {}