package com.br.unifor.orders.domain;

import com.br.unifor.orders.domain.model.Order;

/**
 * Porta para publicar eventos de pedido (ex: pedido criado, pagamento processado).
 */
public interface OrderEventPublisher {
    void publishOrderCreated(Order order);
}