package com.deliverytech.delivery.entity;

import com.deliverytech.delivery.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataPedido;
    private String enderecoEntrega;
    private BigDecimal subtotal;
    private BigDecimal taxaEntrega;
    private BigDecimal valorTotal;
    private String observacoes;
    private String numero;
    private String numeroPedido;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ItemPedido> itens;

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this); // para manter o relacionamento bidirecional consistente
    }

    public void confirmar() {
        this.status = StatusPedido.CONFIRMADO;
    }

    public String getObservacoes() {
    return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @OneToMany(mappedBy = "pedido")
    @JsonIgnore
    private List<ItemPedido> itensPedido;
}
 