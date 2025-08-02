package com.deliverytech.delivery.mapper;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.enums.CategoriaRestaurante;

import org.springframework.stereotype.Component;


@Component
public class RestauranteMapper {

    public Restaurante toEntity(RestauranteRequest request) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(request.getNome());
        restaurante.setTelefone(request.getTelefone());
        restaurante.setEndereco(request.getEndereco());
        
        try {
        CategoriaRestaurante categoria = CategoriaRestaurante.valueOf(request.getCategoria().toUpperCase());
        restaurante.setCategoria(categoria);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Categoria inválida: " + request.getCategoria());
            }


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
