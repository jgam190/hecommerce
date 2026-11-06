package com.example.products.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa um Produto do Catálogo do HeCommerce.
 */
@Document(collection = "produtos")
public class Produto {

    @Id
    private String id;

    @Field(name = "nome")
    private String nome;

    @Field(name = "descricao")
    private String descricao;

    @Field(name = "preco")
    private BigDecimal preco;

    @Field(name = "estoque")
    private Integer estoque;

    @Field(name = "ativo")
    private boolean ativo = true;

    @Field(name = "categoria")
    private String categoria;

    @Field(name = "emoji")
    private String emoji;

    @Field(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Field(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    public Produto() {
        this.dataCadastro = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}
