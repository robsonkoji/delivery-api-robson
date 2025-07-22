package com.deliverytech.delivery.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private boolean disponivel;

    public Produto() {
    // construtor padrão exigido pelo JPA
    }

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @OneToMany(mappedBy = "produto")
    @JsonIgnore
    private List<ItemPedido> itensPedido = new ArrayList<>();

    public Produto(String nome, String descricao, BigDecimal preco, String categoria, boolean disponivel, Restaurante restaurante) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.disponivel = disponivel;
        this.categoria = categoria;
        this.restaurante = restaurante;
    }
}
