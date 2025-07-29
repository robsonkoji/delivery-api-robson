package com.deliverytech.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String categoria;
    private String endereco;
    private String telefone;
    private LocalDateTime dataCriacao = LocalDateTime.now();


    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "taxa_entrega")
    private BigDecimal taxaEntrega;

    public void inativar() {
    this.ativo = false;
    }

    public void reativar() {
    this.ativo = true;
    }

    public BigDecimal getTaxaEntrega() {
    return taxaEntrega;
    }

    public void setTaxaEntrega(BigDecimal taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
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

 