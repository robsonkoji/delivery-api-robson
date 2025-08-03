package com.deliverytech.delivery.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Schema(name = "ItemPedido", description = "Representa um item do pedido com produto, quantidade e preço")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do item do pedido", example = "1")
    private Long id;

    @Schema(description = "Quantidade do produto no pedido", example = "2")
    private int quantidade;

    @Schema(description = "Preço unitário do produto no momento do pedido", example = "19.90")
    private BigDecimal precoUnitario;

    @Schema(description = "Subtotal calculado (preço unitário x quantidade)", example = "39.80")
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @Schema(description = "Pedido ao qual o item pertence")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    @Schema(description = "Produto associado ao item do pedido")
    private Produto produto;

    public ItemPedido() {}

    public ItemPedido(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        this.subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
