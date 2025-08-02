package com.deliverytech.delivery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Objeto de requisição para criação de um pedido")
public class PedidoRequest {

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "ID do cliente que está fazendo o pedido", example = "1")
    private Long clienteId;

    @NotNull(message = "ID do restaurante é obrigatório")
    @Schema(description = "ID do restaurante onde o pedido será feito", example = "10")
    private Long restauranteId;

    @NotBlank(message = "Endereço de entrega é obrigatório")
    @Schema(description = "Endereço completo para entrega do pedido", example = "Av. Paulista, 1000 - CEP 01310-100")
    private String enderecoEntrega;

    @Schema(description = "Observações adicionais para o pedido", example = "Sem cebola, por favor")
    private String observacoes;

    @Valid
    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Schema(description = "Lista de itens do pedido")
    private List<ItemPedidoRequest> itens;
}
