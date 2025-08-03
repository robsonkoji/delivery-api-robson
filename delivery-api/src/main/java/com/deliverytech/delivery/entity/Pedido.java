package com.deliverytech.delivery.entity;

import com.deliverytech.delivery.enums.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Schema(name = "Pedido", description = "Representa um pedido realizado por um cliente em um restaurante")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do pedido", example = "101")
    private Long id;

    @Schema(description = "Data e hora em que o pedido foi realizado", example = "2025-08-03T14:35:00")
    private LocalDateTime dataPedido;

    @Schema(description = "Endereço de entrega do pedido", example = "Av. Paulista, 1234 - São Paulo, SP")
    private String enderecoEntrega;

    @Schema(description = "Subtotal dos itens do pedido (sem a taxa de entrega)", example = "59.90")
    private BigDecimal subtotal;

    @Schema(description = "Taxa de entrega aplicada ao pedido", example = "5.00")
    private BigDecimal taxaEntrega;

    @Schema(description = "Valor total do pedido (subtotal + taxa de entrega)", example = "64.90")
    private BigDecimal valorTotal;

    @Schema(description = "Observações adicionais fornecidas pelo cliente", example = "Por favor, entregar sem contato.")
    private String observacoes;

    @Schema(description = "Data de criação do pedido", example = "2025-08-03T14:35:00")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status atual do pedido", example = "PENDENTE")
    private StatusPedido status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @Schema(description = "Cliente que realizou o pedido")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    @Schema(description = "Restaurante que recebeu o pedido")
    private Restaurante restaurante;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    @Schema(description = "Lista de itens do pedido")
    private List<ItemPedido> itens = new ArrayList<>();

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }

    public void calcularTotais() {
        this.subtotal = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorTotal = subtotal.add(taxaEntrega != null ? taxaEntrega : BigDecimal.ZERO);
    }

    public BigDecimal getTotal() {
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(taxaEntrega != null ? taxaEntrega : BigDecimal.ZERO);
    }
}
