package com.br.unifor.orders;

import com.br.unifor.orders.application.service.impl.DefaultOrderService;
import com.br.unifor.orders.domain.OrderRepository;
import com.br.unifor.orders.domain.OrderEventPublisher;
import com.br.unifor.orders.domain.ProductClient;
import com.br.unifor.orders.domain.model.Order;
import com.br.unifor.orders.domain.model.OrderItemRequest;
import com.br.unifor.orders.domain.model.OrderRequest;
import com.br.unifor.orders.domain.model.OrderResponse;
import com.br.unifor.orders.domain.model.OrderStatus;
import com.br.unifor.orders.domain.model.ProdutoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Teste do serviço de pedido usando Mockito para mockar dependências.
 * Sem dependência de contexto Spring completo.
 */
class DefaultOrderServiceTest {

    private DefaultOrderService orderService;
    private Order mockOrder;
    private ProductClient mockProductClient;
    private OrderRepository mockOrderRepo;
    private OrderEventPublisher mockEventPublisher;

    @BeforeEach
    void setUp() {
        // Criar um pedido mock
        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setUserId("user-123");
        mockOrder.setStatus(OrderStatus.PENDING);
        mockOrder.setTotal(BigDecimal.valueOf(49.99));
        mockOrder.setCreatedAt(java.time.LocalDateTime.now());

        // Mock do OrderRepository com Mockito
        mockOrderRepo = mock(OrderRepository.class);
        when(mockOrderRepo.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        when(mockOrderRepo.findById(anyLong())).thenReturn(Optional.empty());
        when(mockOrderRepo.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(mockOrderRepo.findByUserId(anyString())).thenReturn(new ArrayList<>());
        when(mockOrderRepo.existsById(anyLong())).thenReturn(false);
        when(mockOrderRepo.existsById(1L)).thenReturn(true);
        when(mockOrderRepo.findActiveById(anyLong())).thenReturn(Optional.empty());
        when(mockOrderRepo.findActiveById(1L)).thenReturn(Optional.of(mockOrder));

        // Mock do ProductClient com Mockito
        mockProductClient = mock(ProductClient.class);
        ProdutoDto produtoDto = new ProdutoDto();
        produtoDto.setId("produto-1");
        produtoDto.setNome("Produto Teste");
        produtoDto.setPreco(BigDecimal.valueOf(19.99));
        produtoDto.setAtivo(true);
        when(mockProductClient.findProductById("produto-1")).thenReturn(produtoDto);
        when(mockProductClient.findProductById(anyString())).thenReturn(produtoDto);

        // Publisher de eventos mock
        mockEventPublisher = mock(OrderEventPublisher.class);

        // Instanciar o serviço com os mocks
        this.orderService = new DefaultOrderService(mockOrderRepo, mockProductClient, mockEventPublisher);
    }

    @Test
    void serviceInstanceCreated() {
        assertNotNull(orderService, "OrderService não deve ser nulo");
    }

    @Test
    void shouldFindOrderById() {
        Optional<Order> order = mockOrderRepo.findById(1L);
        assertTrue(order.isPresent());
        assertEquals("user-123", order.get().getUserId());
    }

    @Test
    void shouldCalculateTotalWhenCreatingOrder() {
        OrderRequest request = new OrderRequest();
        request.setUserId("user-123");
        if (request.getItems() == null) {
            request.setItems(new ArrayList<>());
        }
        request.getItems().add(new OrderItemRequest());
        request.getItems().get(0).setProductId("produto-1");
        request.getItems().get(0).setQuantity(2);

        OrderResponse response = orderService.createOrder(request);
        assertNotNull(response);
        assertEquals("user-123", response.getUserId());
        assertNotNull(response.getStatus());
        assertEquals(new BigDecimal("39.98"), response.getTotal());
        assertEquals(1, response.getItems().size());
        assertEquals("produto-1", response.getItems().get(0).getProductId());
        assertEquals(2, response.getItems().get(0).getQuantity());
        verify(mockOrderRepo).save(argThat(order ->
                order.getItems().size() == 1
                        && order.getItems().get(0).getOrder() == order
                        && "produto-1".equals(order.getItems().get(0).getProductId())
                        && new BigDecimal("39.98").equals(order.getItems().get(0).getSubtotal())));
        verify(mockEventPublisher).publishOrderCreated(any(Order.class));
    }

    @Test
    void shouldRejectEmptyOrder() {
        OrderRequest request = new OrderRequest();
        request.setUserId("user-123");
        request.setItems(List.of());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
        verify(mockOrderRepo, never()).save(any(Order.class));
    }
}
