package com.deliverytech.delivery.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ItemPedidoResponse {
    private Long id;
    private Long produtoId;
    private String nomeProduto;
    private int quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;
}
