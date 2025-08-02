package com.deliverytech.delivery.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class RestauranteResponse {
    private Long id;
    private String nome;
    private String categoria;
    private String endereco;
    private BigDecimal taxaEntrega;
    private boolean ativo;
    private String telefone;
    private Integer tempoEntrega;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime dataCriacao;
}