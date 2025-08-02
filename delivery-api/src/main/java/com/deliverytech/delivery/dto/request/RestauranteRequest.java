package com.deliverytech.delivery.dto.request;

import com.deliverytech.delivery.enums.CategoriaRestaurante;
import com.deliverytech.delivery.validation.ValidCategoria;
import com.deliverytech.delivery.validation.ValidCEP;
import com.deliverytech.delivery.validation.ValidHorarioFuncionamento;
import com.deliverytech.delivery.validation.ValidTelefone;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestauranteRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Schema(example = "Restaurante da Esquina")
    private String nome;

    @NotBlank(message = "Categoria é obrigatória")
    @ValidCategoria(enumClass = CategoriaRestaurante.class)
    @Schema(example = "BRASILEIRA", description = "Categoria do restaurante (ex: BRASILEIRA, JAPONESA)")
    private String categoria;

    @NotNull(message = "Taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "A taxa de entrega deve ser maior que zero")
    @Schema(example = "6.99")
    private BigDecimal taxaEntrega;

    @NotBlank(message = "Telefone é obrigatório")
    @ValidTelefone
    @Schema(example = "11999998888", description = "Telefone com 10 ou 11 dígitos numéricos")
    private String telefone;

    @NotBlank(message = "Endereço é obrigatório")
    @ValidCEP
    @Schema(example = "Rua das Flores, 123 - 01000-000", description = "Deve conter um CEP válido no formato 00000-000")
    private String endereco;

    @NotNull(message = "Tempo de entrega é obrigatório")
    @Min(value = 10, message = "Tempo de entrega deve ser no mínimo 10 minutos")
    @Max(value = 120, message = "Tempo de entrega deve ser no máximo 120 minutos")
    @Schema(example = "45")
    private Integer tempoEntrega;

    @NotBlank(message = "Horário de funcionamento é obrigatório")
    @ValidHorarioFuncionamento
    @Schema(example = "10:00-22:00", description = "Formato HH:MM-HH:MM")
    private String horarioFuncionamento;

    @Schema(example = "true")
    private Boolean ativo = true;
}
