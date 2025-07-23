package com.deliverytech.delivery.model;

import com.deliverytech.delivery.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@ToString(exclude = "itens")
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
    private LocalDateTime dataCriacao;



    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ItemPedido> itens;

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }

    public void confirmar() {
        this.status = StatusPedido.CONFIRMADO;
    }

    public void calcularValores() {
        this.subtotal = itens.stream()
            .map(ItemPedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (taxaEntrega == null) {
            taxaEntrega = BigDecimal.ZERO;
        }

        this.valorTotal = subtotal.add(taxaEntrega);
    }

   public void setDataCriacao(LocalDateTime dataCriacao) {
    this.dataCriacao = dataCriacao;
   }

    public void calcularValorTotal() {
        this.valorTotal = itens.stream()
            .map(ItemPedido::getPrecoTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
