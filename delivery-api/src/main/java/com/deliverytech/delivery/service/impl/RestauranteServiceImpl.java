package com.deliverytech.delivery.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.mapper.RestauranteMapper;
import com.deliverytech.delivery.repository.RestauranteRepository;
import com.deliverytech.delivery.service.RestauranteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final RestauranteMapper mapper;

    @Override
    public RestauranteResponse cadastrarRestaurante(RestauranteRequest request) {
        Restaurante restaurante = mapper.toEntity(request);
        restaurante.setAtivo(true);
        return mapper.toResponse(restauranteRepository.save(restaurante));
    }

    @Override
    public RestauranteResponse buscarRestaurantePorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        return mapper.toResponse(restaurante);
    }

    @Override
    public RestauranteResponse atualizarRestaurante(Long id, RestauranteRequest request) {
        Restaurante restaurante = restauranteRepository.findById(id)
             .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

        restaurante.setNome(request.getNome());
        restaurante.setCategoria(request.getCategoria());
        restaurante.setEndereco(request.getEndereco());
        restaurante.setTelefone(request.getTelefone());
        restaurante.setTaxaEntrega(request.getTaxaEntrega());

        return mapper.toResponse(restauranteRepository.save(restaurante));
    }

    @Override
    public List<RestauranteResponse> buscarRestaurantesPorCategoria(String categoria) {
        return restauranteRepository.findByCategoriaIgnoreCase(categoria)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<RestauranteResponse> buscarRestaurantesDisponiveis() {
        return restauranteRepository.findByAtivoTrue()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
        .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + restauranteId));

        if (cep.startsWith("01")) {
            return restaurante.getTaxaEntrega().multiply(BigDecimal.valueOf(0.5)); // Exemplo: 50% de desconto
        } else {
            return restaurante.getTaxaEntrega();
        }
    }
}
