package com.deliverytech.delivery.dto.request;

import java.math.BigDecimal;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProdutoRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    @Schema(description = "Nome do produto", example = "Pizza Margherita")
    private String nome;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 500, message = "Descrição deve ter no mínimo 10 caracteres")
    @Schema(description = "Descrição do produto", example = "Pizza artesanal com molho de tomate e manjericão fresco.")
    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", inclusive = true, message = "Preço deve ser maior que zero")
    @DecimalMax(value = "500.00", inclusive = true, message = "Preço não pode exceder R$ 500")
    @Schema(description = "Preço do produto", example = "49.90")
    private BigDecimal preco;

    @NotNull(message = "Categoria é obrigatória")
    @Schema(description = "Categoria do produto", example = "PIZZAS")
    private String categoria;

    @NotNull(message = "Disponibilidade é obrigatória")
    @Schema(description = "Indica se o produto está disponível para pedidos", example = "true")
    private Boolean disponivel;

    @NotNull(message = "ID do restaurante é obrigatório")
    @Schema(description = "Identificador do restaurante ao qual o produto pertence", example = "1")
    private Long restauranteId;

    private int estoque;
}
