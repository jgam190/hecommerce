package com.br.unifor.orders.domain;

import com.br.unifor.orders.domain.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * Porta de persistência para Pedidos.
 * Estende JpaRepository para obter métodos CRUD automáticos e
 * define consultas personalizadas via método nome ou @Query.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Busca pedidos por userId (usando convenção de nome do Spring Data).
     */
    List<Order> findByUserId(String userId);

    /**
     * Verifica se um pedido existe pelo ID.
     */
    boolean existsById(Long id);

    /**
     * Busca um pedido ativo pelo ID (status PENDING ou PAID).
     * Usa @Query para condição mais complexa.
     */
    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.status IN ('PENDING', 'PAID')")
    Optional<Order> findActiveById(@Param("id") Long id);
}