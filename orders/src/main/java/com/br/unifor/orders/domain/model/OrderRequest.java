package com.br.unifor.orders.domain.model;

import java.util.List;

/**
 * DTO de entrada para criar um pedido.
 */
public class OrderRequest {
    private String userId;
    private List<OrderItemRequest> items;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}