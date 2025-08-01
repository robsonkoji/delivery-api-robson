package com.deliverytech.delivery.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ProdutoRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    @Schema(description = "Nome do produto", example = "produto1")
    private String nome;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500)
    @Schema(description = "Descrição do produto", example = "descricao1")
    private String descricao;

    @DecimalMin(value = "0.0", inclusive = false, message = "Preço deve ser maior que zero")
    @Schema(description = "Preço do produto", example = "10.00")
    private BigDecimal preco;

    @NotBlank(message = "Categoria é obrigatória")
    @Size(max = 50)
    @Schema(description = "Categoria do produto", example = "categoria1")
    private String categoria;

    @NotNull(message = "Disponibilidade é obrigatória")
    @Schema(description = "Disponibilidade do produto", example = "true")
    private Boolean disponivel;

    @NotNull(message = "ID do restaurante é obrigatório")
    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1")
    private Long restauranteId;
    
}
