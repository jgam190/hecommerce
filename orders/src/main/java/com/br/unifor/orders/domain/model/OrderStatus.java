package com.br.unifor.orders.domain.model;

/**
 * Status possível de um pedido.
 */
public enum OrderStatus {
    PENDING,
    PAID,
    CANCELLED,
    SHIPPED,
    DELIVERED
}