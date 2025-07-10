package com.deliverytech.delivery.entity;

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



    @OneToMany(mappedBy = "restaurante")
    @JsonIgnore
    private List<Produto> produtos;

    @OneToMany(mappedBy = "restaurante")
    @JsonIgnore
    private List<Pedido> pedidos;

    @Column(name = "avaliacao")
    private BigDecimal avaliacao;


    

}

 