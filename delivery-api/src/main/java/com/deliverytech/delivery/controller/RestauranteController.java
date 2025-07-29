package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.service.RestauranteService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;



@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }
   
    // POST /api/restaurantes - Cadastrar restaurante
    @PostMapping
    public ResponseEntity<RestauranteResponse> cadastrar(@Valid @RequestBody RestauranteRequest request) {
        RestauranteResponse restaurante = restauranteService.cadastrarRestaurante(request);
        return ResponseEntity.created(URI.create("/api/restaurantes/" + restaurante.getId()))
            .body(restaurante);
    }

    // GET /api/restaurantes/{id} - Buscar por ID
    @GetMapping("/{id}")
        public ResponseEntity<RestauranteResponse> buscarPorId(@PathVariable Long id) {
        RestauranteResponse restaurante = restauranteService.buscarRestaurantePorId(id);
        return ResponseEntity.ok(restaurante);
    }

    // GET /api/restaurantes - Listar disponíveis (com paginação e filtros opcionais)
    @GetMapping
    public ResponseEntity<List<RestauranteResponse>> listarDisponiveis(@RequestBody RestauranteRequest request) {
        List<RestauranteResponse> restaurantes = restauranteService.buscarRestaurantesDisponiveis();
        return ResponseEntity.ok(restaurantes);
        
    }

    // GET /api/restaurantes/categoria/{categoria} - Listar por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponse>> listarPorCategoria(@PathVariable String categoria) {
        List<RestauranteResponse> restaurantes = restauranteService.buscarRestaurantesPorCategoria(categoria);
        return ResponseEntity.ok(restaurantes);
    }

    // PUT /api/restaurantes/{id} - Atualizar restaurante
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponse> atualizar(@PathVariable Long id, @RequestBody RestauranteRequest request) {
        RestauranteResponse restauranteAtualizado = restauranteService.atualizarRestaurante(id, request);
        return ResponseEntity.ok(restauranteAtualizado);
    }

    // GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa de entrega
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxaEntrega(@PathVariable Long id, @PathVariable String cep) {
        BigDecimal taxaEntrega = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(taxaEntrega);
    }
    
}