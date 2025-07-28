package com.deliverytech.delivery.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.exception.EntityNotFoundException;
import com.deliverytech.delivery.repository.RestauranteRepository;
import com.deliverytech.delivery.service.RestauranteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestauranteServiceImpl implements RestauranteService {

    private final RestauranteRepository restauranteRepository;

    @Override
    public Restaurante cadastrarRestaurante(RestauranteRequest request) {
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(restaurante.getNome());
        restaurante.setCategoria(restaurante.getCategoria());
        restaurante.setEndereco(restaurante.getEndereco());
        restaurante.setTelefone(restaurante.getTelefone());
        restaurante.setTaxaEntrega(restaurante.getTaxaEntrega());
        restaurante.setAtivo(true);
        return restauranteRepository.save(restaurante);
    }

    @Override
    public Restaurante buscRestaurantePorId(Long id) {
       return restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));
    }

    @Override
    public List<Restaurante> buscaRestaurantePorCategoria(String categoria) {
        return restauranteRepository.findByCategoriaIgnoreCase(categoria);
    }

    @Override
    public List<Restaurante> buscaRestauranteDisponiveis() {
        return restauranteRepository.findByAtivoTrue();
    }

    @Override
    public Restaurante atualizarRestaurante(Long id, RestauranteRequest request) {
        Restaurante restaurante = buscRestaurantePorId(id);
        restaurante.setNome(request.getNome());
        restaurante.setCategoria(request.getCategoria());
        restaurante.setEndereco(request.getEndereco());
        restaurante.setTelefone(request.getTelefone());
        restaurante.setTaxaEntrega(request.getTaxaEntrega());
        return restauranteRepository.save(restaurante);
    }

    @Override
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = buscRestaurantePorId(restauranteId);
        // Implementar lógica para calcular a taxa de entrega com base no CEP
        if (cep.startsWith("01")) {
            return restaurante.getTaxaEntrega().multiply(BigDecimal.valueOf(5.0)); // Exemplo de desconto para CEPs que começam com "01"
        }
        else {
            return restaurante.getTaxaEntrega();
        }
    }
}
