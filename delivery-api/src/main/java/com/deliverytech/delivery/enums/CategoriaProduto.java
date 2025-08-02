package com.deliverytech.delivery.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Categorias disponíveis para produtos")
public enum CategoriaProduto {
    PIZZAS,
    MASSAS,
    SUSHI,
    LANCHE,
    BEBIDA,
    SOBREMESA,
    SALADA,
    VEGANO
}
