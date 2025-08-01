package com.deliverytech.delivery.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class PedidoResponse {
    private Long id;
    private ClienteResponse cliente;
    private Long restauranteId;
    private String enderecoEntrega;
    private String observacoes;
    private String status;
    private LocalDateTime dataPedido;
    private BigDecimal taxaEntrega;
    private BigDecimal total;
    private List<ItemPedidoResponse> itens;
}
