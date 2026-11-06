package com.example.products.repository;

import com.example.products.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade Produto.
 */
@Repository
public interface ProdutoRepository extends MongoRepository<Produto, String> {

    /**
     * Busca produtos ativos (paginado).
     */
    Page<Produto> findByAtivoTrue(Pageable pageable);

    /**
     * Busca produtos pelo nome (parcial, ignorando maiúsculas/minúsculas).
     */
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca produtos pelo intervalo de preço.
     */
    List<Produto> findByPrecoBetween(BigDecimal menor, BigDecimal maior);

    /**
     * Verifica se existe um produto com o nome informado.
     */
    boolean existsByNomeIgnoreCase(String nome);

    /**
     * Busca um produto ativo pelo ID.
     */
    Optional<Produto> findByIdAndAtivoTrue(String id);
}
