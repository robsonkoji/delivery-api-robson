package com.deliverytech.delivery.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime dataCriacao;  
}
