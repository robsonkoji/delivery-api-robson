package com.deliverytech.delivery.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery.enums.StatusPedido;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Objeto de resposta com os dados do pedido")
public class PedidoResponse {

    @Schema(example = "1001")
    private Long id;

    private ClienteResponse cliente;
    private RestauranteResponse restaurante; 

    @Schema(example = "10")
    private Long restauranteId;

    @Schema(example = "Av. Paulista, 1000 - CEP 01310-100")
    private String enderecoEntrega;

    @Schema(example = "Sem cebola, por favor")
    private String observacoes;

    @Schema(example = "CRIADO")
    private StatusPedido status;

    @Schema(example = "2025-08-02T13:00:00")
    private LocalDateTime dataPedido;

    @Schema(example = "7.50")
    private BigDecimal taxaEntrega;

    @Schema(example = "57.40")
    private BigDecimal total;

    private List<ItemPedidoResponse> itens;
}
