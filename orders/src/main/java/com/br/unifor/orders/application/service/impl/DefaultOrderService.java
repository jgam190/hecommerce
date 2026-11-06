package com.br.unifor.orders.application.service.impl;

import com.br.unifor.orders.application.service.OrderService;
import com.br.unifor.orders.domain.model.Order;
import com.br.unifor.orders.domain.OrderRepository;
import com.br.unifor.orders.domain.ProductClient;
import com.br.unifor.orders.domain.OrderEventPublisher;
import com.br.unifor.orders.domain.model.OrderItem;
import com.br.unifor.orders.domain.model.OrderRequest;
import com.br.unifor.orders.domain.model.OrderResponse;
import com.br.unifor.orders.domain.model.OrderItemResponse;
import com.br.unifor.orders.domain.model.ProdutoDto;
import com.br.unifor.orders.domain.model.OrderItemRequest;
import com.br.unifor.orders.domain.model.OrderStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventPublisher orderEventPublisher;

    public DefaultOrderService(OrderRepository orderRepository,
                               ProductClient productClient,
                               OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        if (request == null || request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("Usuário do pedido é obrigatório");
        }

        List<OrderItemRequest> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter ao menos um item");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : items) {
            if (itemRequest == null || itemRequest.getProductId() == null
                    || itemRequest.getProductId().isBlank()) {
                throw new IllegalArgumentException("ID do produto é obrigatório");
            }
            Integer quantidade = itemRequest.getQuantity();
            if (quantidade == null || quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade inválida para produto: " + itemRequest.getProductId());
            }

            ProdutoDto produto = productClient.findProductById(itemRequest.getProductId());
            if (produto == null) {
                throw new IllegalArgumentException("Produto não encontrado: " + itemRequest.getProductId());
            }
            if (!produto.isAtivo()) {
                throw new IllegalArgumentException("Produto inativo: " + itemRequest.getProductId());
            }
            if (produto.getPreco() == null || produto.getPreco().signum() <= 0) {
                throw new IllegalArgumentException("Preço inválido para produto: " + itemRequest.getProductId());
            }

            BigDecimal precoUnitario = produto.getPreco();
            BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
            total = total.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setQuantity(quantidade);
            orderItem.setUnitPrice(precoUnitario);
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(total);
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        Order saved = orderRepository.save(order);
        orderEventPublisher.publishOrderCreated(saved);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + id));
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(DefaultOrderService::toResponse).toList();
    }

    private static OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setStatus(order.getStatus());
        response.setTotal(order.getTotal());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(order.getItems().stream().map(item -> {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setProductId(item.getProductId());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setSubtotal(item.getSubtotal());
            return itemResponse;
        }).toList());
        return response;
    }
}