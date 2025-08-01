package com.deliverytech.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RelatorioClienteAtivoResponse {

    @Schema(description = "ID do cliente")
    private Long clienteId;

    @Schema(description = "Nome do cliente")
    private String nomeCliente;

    @Schema(description = "Número de pedidos realizados")
    private Long totalPedidos;
}
