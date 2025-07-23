package com.deliverytech.delivery.service;

import com.deliverytech.delivery.model.Restaurante;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    /*
     * Buscar restaurantes por categoria
     */
    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria);
    }

    /**
     * Listar todos os restaurantes ativos
     */
    @Transactional(readOnly = true)
    public List<Restaurante> listarAtivos() {
        return restauranteRepository.findByAtivoTrue();
    }

    /**
     * Buscar restaurantes por taxa de entrega menor ou igual
     */
    @Transactional(readOnly = true)
    public List<Restaurante> buscarPorTaxaEntregaMenorOuIgual(BigDecimal taxa) {
        return restauranteRepository.findByTaxaEntregaLessThanEqual(taxa);
    }

    /**
     * Buscar os 5 primeiros restaurantes por nome (ordem alfabética)
     */
    @Transactional(readOnly = true)
    public List<Restaurante> buscarTop5PorNome() {
        return restauranteRepository.findTop5ByOrderByNomeAsc();
    }


    /**
     * Cadastrar novo restaurante
     */
    public Restaurante cadastrar(Restaurante restaurante) {
        validarDadosRestaurante(restaurante);
        restaurante.setAtivo(true);
        return restauranteRepository.save(restaurante);
    }

    /**
     * Buscar restaurante por ID
     */
    @Transactional(readOnly = true)
    public Optional<Restaurante> buscarPorId(Long id) {
        return restauranteRepository.findById(id);
    }

    /**
     * Atualizar dados do restaurante
     */
    public Restaurante atualizar(Long id, Restaurante restauranteAtualizado) {
        Restaurante restaurante = buscarPorId(id)
            .orElseThrow(() -> notFoundRestaurante(id));

        validarDadosRestaurante(restauranteAtualizado);

        restaurante.setNome(restauranteAtualizado.getNome());
        restaurante.setEndereco(restauranteAtualizado.getEndereco());
        restaurante.setTelefone(restauranteAtualizado.getTelefone());

        return restauranteRepository.save(restaurante);
    }

    private IllegalArgumentException notFoundRestaurante(Long id) {
        return new IllegalArgumentException("Restaurante não encontrado: " + id);
    }

    /**
     * Ativar restaurante
     */
    public void ativar(Long id) {
        Restaurante restaurante = buscarPorId(id)
            .orElseThrow(() -> notFoundRestaurante(id));
        restaurante.setAtivo(true);
        restauranteRepository.save(restaurante);
    }

    /**
     * Inativar restaurante
     */
    public void inativar(Long id) {
        Restaurante restaurante = buscarPorId(id)
            .orElseThrow(() -> notFoundRestaurante(id));
        restaurante.setAtivo(false);
        restauranteRepository.save(restaurante);
    }

    private void validarDadosRestaurante(Restaurante restaurante) {
        if (restaurante.getNome() == null || restaurante.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do restaurante é obrigatório");
        }
        if (restaurante.getEndereco() == null || restaurante.getEndereco().trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço é obrigatório");
        }
    }
}
