package com.br.unifor.notifications.mensageria.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Evento de pedido publicado no Kafka.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PedidoEvent(
        String orderId,
        String clienteId,
        String status,
        BigDecimal valorTotal
) {}