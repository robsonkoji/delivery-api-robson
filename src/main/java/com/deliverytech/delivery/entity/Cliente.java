package com.deliverytech.delivery.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data // Anotacao Lombok para getters, setters, etc.
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private boolean ativo;

    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos;

    public void inativar() {
    this.ativo = false;
}
}