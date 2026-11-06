package com.br.unifor.orders.application.service;

import com.br.unifor.orders.domain.model.Order;
import com.br.unifor.orders.domain.model.OrderStatus;
import com.br.unifor.orders.domain.model.OrderRequest;
import com.br.unifor.orders.domain.model.OrderResponse;
import java.util.List;

/**
 * Porta com as regras de negócio para Pedidos.
 */
public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getOrdersByUser(String userId);
}