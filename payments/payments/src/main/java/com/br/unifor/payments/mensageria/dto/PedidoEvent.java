package com.br.unifor.payments.mensageria.dto;

import java.math.BigDecimal;

public record PedidoEvent(
        String orderId,
        String clienteId,
        String status,
        BigDecimal valorTotal
) {}
