package com.deliverytech.delivery.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Entity
@Data
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String categoria;
    private String endereco;
    private String telefone;
    private BigDecimal taxaEntrega;
    private boolean ativo;

    public void inativar() {
    this.ativo = false;
    }

    public void reativar() {
    this.ativo = true;
    }

    public Restaurante() {
    }

    public Restaurante(Long id, String nome, String categoria, String endereco, String telefone, BigDecimal taxaEntrega, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.endereco = endereco;
        this.telefone = telefone;
        this.taxaEntrega = taxaEntrega;
        this.ativo = ativo;
        }



    @OneToMany(mappedBy = "restaurante")
    @JsonIgnore
    private List<Produto> produtos;

    @OneToMany(mappedBy = "restaurante")
    @JsonIgnore
    private List<Pedido> pedidos;

    @Column(name = "avaliacao")
    private BigDecimal avaliacao;


    

}

 