package com.br.unifor.orders;

import com.br.unifor.orders.application.service.OrderService;
import com.br.unifor.orders.controller.OrderController;
import com.br.unifor.orders.domain.model.OrderRequest;
import com.br.unifor.orders.domain.model.OrderResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste unitário simplificado do controller.
 * Usa um OrderService "mock" simples para injetar no controller.
 */
class OrderControllerTest {

    // OrderService "mock" - implementação mínima
    private static class MockOrderService implements OrderService {
        @Override
        public OrderResponse createOrder(OrderRequest request) {
            return new OrderResponse();
        }

        @Override
        public OrderResponse getOrderById(Long id) {
            return new OrderResponse();
        }

        @Override
        public List<OrderResponse> getOrdersByUser(String userId) {
            return Collections.emptyList();
        }
    }

    @Test
    void controllerCanBeInstantiatedWithMockService() {
        OrderService mockService = new MockOrderService();
        OrderController controller = new OrderController(mockService);
        assertNotNull(controller, "Controller deve ser instanciado com o serviço");
    }
}
