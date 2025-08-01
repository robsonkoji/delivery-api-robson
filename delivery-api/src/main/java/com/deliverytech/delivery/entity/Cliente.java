package com.deliverytech.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Data // Anotacao Lombok para getters, setters, etc.
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private boolean ativo;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pedido> pedidos;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;
    
    public void inativar() {
    this.ativo = false;
    }

    public void reativar() {
    this.ativo = true;
    }

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }

    public Cliente(Long id, String nome, String email, String telefone, String endereco, boolean ativo) {
    this.id = id;
    this.nome = nome;
    this.email = email;
    this.telefone = telefone;
    this.endereco = endereco;
    this.ativo = ativo;
}
}