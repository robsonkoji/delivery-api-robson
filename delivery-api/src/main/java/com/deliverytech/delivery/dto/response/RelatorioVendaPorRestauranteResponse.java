package com.deliverytech.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RelatorioVendaPorRestauranteResponse {

    @Schema(description = "ID do restaurante")
    private Long restauranteId;

    @Schema(description = "Nome do restaurante")
    private String nomeRestaurante;

    @Schema(description = "Valor total de vendas")
    private BigDecimal valorTotal;

    @Schema(description = "Total de pedidos")
    private Long totalPedidos;
}
