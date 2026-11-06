package com.br.unifor.orders.domain.model;

import java.math.BigDecimal;

/**
 * DTO simplificado para transferência de produto do catálogo.
 * Apenas os campos necessários para o serviço de Pedidos (preço).
 */
public class ProdutoDto {
    private String id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer estoque;
    private boolean ativo;
    private java.time.LocalDateTime dataCadastro;
    private java.time.LocalDateTime dataAtualizacao;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public java.time.LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(java.time.LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public java.time.LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(java.time.LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}