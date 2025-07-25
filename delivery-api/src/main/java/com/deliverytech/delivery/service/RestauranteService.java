package com.deliverytech.delivery.service;

import java.math.BigDecimal;
import java.util.List;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.entity.Restaurante;

public interface RestauranteService {

    Restaurante cadastrarRestaurante(RestauranteRequest request);

    Restaurante buscRestaurantePorId (Long id);

    List<Restaurante> buscRestaurantePorCategoria (String categoria);

    List<Restaurante> buscRestauranteDisponiveis ();

    Restaurante atualizarRestaurante(Long id, RestauranteRequest request);

    BigDecimal calcularTaxaEntrega (Long restauranteId, String cep);
}
    
    