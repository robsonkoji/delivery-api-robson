package com.deliverytech.delivery.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data // Anotacao Lombok para getters, setters, etc.
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private boolean ativo;

    public Cliente() {
    }
    
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Pedido> pedidos;

    public void inativar() {
    this.ativo = false;
    }

    public void reativar() {
    this.ativo = true;
    }


    public Cliente(Long id, String nome, String email, String telefone, boolean ativo) {
    this.id = id;
    this.nome = nome;
    this.email = email;
    this.telefone = telefone;
    this.ativo = ativo;
}
}