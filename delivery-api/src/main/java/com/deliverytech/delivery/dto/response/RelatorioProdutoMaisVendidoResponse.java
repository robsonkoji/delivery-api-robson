package com.deliverytech.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RelatorioProdutoMaisVendidoResponse {

    @Schema(description = "ID do produto")
    private Long produtoId;

    @Schema(description = "Nome do produto")
    private String nomeProduto;

    @Schema(description = "Quantidade vendida")
    private Long quantidadeVendida;
}
