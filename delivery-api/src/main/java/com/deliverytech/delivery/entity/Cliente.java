package com.deliverytech.delivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "Cliente", description = "Representa um cliente da aplicação")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do cliente", example = "1")
    private Long id;

    @Schema(description = "Nome completo do cliente", example = "Maria da Silva")
    private String nome;

    @Schema(description = "Email do cliente", example = "maria.silva@example.com")
    private String email;

    @Schema(description = "Número de telefone do cliente", example = "(11) 99999-1234")
    private String telefone;

    @Schema(description = "Endereço completo do cliente", example = "Rua Exemplo, 123 - São Paulo/SP")
    private String endereco;

    @Schema(description = "Indica se o cliente está ativo", example = "true")
    private boolean ativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @Schema(description = "Usuário associado ao cliente")
    private Usuario usuario;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @JsonIgnore
    @Schema(description = "Pedidos feitos pelo cliente")
    private List<Pedido> pedidos;

    @Column(name = "data_criacao", updatable = false)
    @Schema(description = "Data de criação do cliente", example = "2025-08-03T14:30:00")
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
