package com.deliverytech.delivery.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.NotNull;


@Data
public class RestauranteRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @DecimalMin(value = "0.0", inclusive = true, message = "A taxa de entrega deve ser positiva")
    @NotNull(message = "Taxa de entrega é obrigatória")
    private BigDecimal taxaEntrega;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    
}
