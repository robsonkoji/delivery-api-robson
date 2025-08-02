package com.deliverytech.delivery.mapper;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.entity.Restaurante;



import org.springframework.stereotype.Component;


@Component
public class RestauranteMapper {

    public Restaurante toEntity(RestauranteRequest request) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(request.getNome());
        restaurante.setTelefone(request.getTelefone());
        restaurante.setEndereco(request.getEndereco());
        restaurante.setCategoria(request.getCategoria());
        restaurante.setTaxaEntrega(request.getTaxaEntrega());
        return restaurante;
    }

    public RestauranteResponse toResponse(Restaurante restaurante) {
        RestauranteResponse response = new RestauranteResponse();
        response.setId(restaurante.getId());
        response.setNome(restaurante.getNome());
        response.setTelefone(restaurante.getTelefone());
        response.setEndereco(restaurante.getEndereco());
        response.setCategoria(restaurante.getCategoria());
        response.setTaxaEntrega(restaurante.getTaxaEntrega());
        response.setAtivo(restaurante.getAtivo());
        response.setDataCriacao(restaurante.getDataCriacao());
        return response;
    }
}
