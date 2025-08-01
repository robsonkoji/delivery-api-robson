package com.deliverytech.delivery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Item de um pedido, contendo produto e quantidade")
public class ItemPedidoRequest {
    
    @NotNull(message = "ID do produto é obrigatório")
    @Schema(description = "ID do produto selecionado", example = "1")
    private Long produtoId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    @Schema(description = "Quantidade do produto solicitado", example = "1", minimum = "1")
    private Integer quantidade;

    // ✅ Construtor necessário para os testes
    public ItemPedidoRequest(Long produtoId, Integer quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }
}