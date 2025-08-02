package com.deliverytech.delivery.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProdutoResponse {

    @Schema(description = "ID do produto", example = "100")
    private Long id;

    @Schema(description = "Nome do produto", example = "Pizza Margherita")
    private String nome;

    @Schema(description = "Descrição do produto", example = "Pizza artesanal com molho de tomate e manjericão fresco.")
    private String descricao;

    @Schema(description = "Preço do produto", example = "49.90")
    private BigDecimal preco;

    @Schema(description = "Categoria do produto", example = "PIZZAS")
    private String categoria;

    @Schema(description = "Disponibilidade do produto", example = "true")
    private boolean disponivel;

    @Schema(description = "ID do restaurante associado", example = "1")
    private Long restauranteId;
}
