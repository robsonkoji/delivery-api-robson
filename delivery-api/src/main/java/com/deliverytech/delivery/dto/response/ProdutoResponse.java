package com.deliverytech.delivery.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProdutoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private boolean disponivel;
    private Long restauranteId;
}
