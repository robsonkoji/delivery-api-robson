package com.deliverytech.delivery.service;

import java.math.BigDecimal;
import java.util.List;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;

public interface RestauranteService {
    
    RestauranteResponse cadastrarRestaurante(RestauranteRequest request);

    RestauranteResponse buscarRestaurantePorId(Long id); // deve estar EXATAMENTE com esse nome e assinatura

    RestauranteResponse atualizarRestaurante(Long id, RestauranteRequest request);

    List<RestauranteResponse> buscarRestaurantesPorCategoria(String categoria);

    List<RestauranteResponse> buscarRestaurantesDisponiveis();

    BigDecimal calcularTaxaEntrega(Long restauranteId, String cep);

    List<RestauranteResponse> buscarRestaurantesComFiltros(String categoria, Boolean ativo);

    RestauranteResponse alterarStatusRestaurante(Long id);

    List<RestauranteResponse> buscarRestaurantesProximos(String cep);

    boolean isOwner(Long restauranteId);

    void removerRestaurante(Long id);

}
