package com.deliverytech.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RelatorioPedidosPorPeriodoResponse {

    @Schema(description = "Data de início do período", example = "2025-07-01")
    private String dataInicio;

    @Schema(description = "Data de fim do período", example = "2025-07-30")
    private String dataFim;

    @Schema(description = "Quantidade total de pedidos no período")
    private Long totalPedidos;

    @Schema(description = "Valor total dos pedidos no período")
    private BigDecimal valorTotal;
}
