package com.deliverytech.delivery.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@Data
public class RestauranteRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(example = "restaurante1")
    private String nome;

    @NotBlank(message = "Categoria é obrigatória")
    @Schema(example = "categoria1")
    private String categoria;

    @DecimalMin(value = "0.0", inclusive = true, message = "A taxa de entrega deve ser positiva")
    @NotNull(message = "Taxa de entrega é obrigatória")
    @Schema(example = "5.90")
    private BigDecimal taxaEntrega;

    @NotBlank(message = "Telefone é obrigatório")
    @Schema(example = "(11) 99999-9999")
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    @Schema(example = "endereco1, 123 - cep 10000-000")
    // Exemplo simples para validar CEP brasileiro: 5 dígitos + hífen + 3 dígitos opcional
    @Pattern(regexp = ".*\\d{5}-?\\d{3}.*", message = "O endereço deve conter um CEP válido")
    private String endereco;

    

    private Boolean ativo = true;

}
