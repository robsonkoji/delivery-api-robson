package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.mapper.RestauranteMapper;
import com.deliverytech.delivery.service.RestauranteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;



@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;
    private final RestauranteMapper mapper;

    public RestauranteController(RestauranteService restauranteService, RestauranteMapper mapper) {
        this.restauranteService = restauranteService;
        this.mapper = mapper;
    }
   
    // POST /api/restaurantes - Cadastrar restaurante
    @PostMapping
    public ResponseEntity<RestauranteResponse> cadastrar(@RequestBody RestauranteRequest request) {
        Restaurante restaurante = restauranteService.cadastrarRestaurante(request);
        return ResponseEntity.created(URI.create("/api/restaurantes/" + restaurante.getId()))
            .body(mapper.toResponse(restaurante));
    }

    // GET /api/restaurantes/{id} - Buscar por ID
    @GetMapping("/{id}")
        public ResponseEntity<RestauranteResponse> buscarPorId(@PathVariable Long id) {
        Restaurante restaurante = restauranteService.buscRestaurantePorId(id);
        return ResponseEntity.ok(mapper.toResponse(restaurante));
    }

    // GET /api/restaurantes - Listar disponíveis (com paginação e filtros opcionais)
    @GetMapping
    public ResponseEntity<List<Restaurante>> listarDisponiveis(@RequestBody RestauranteRequest request) {
        List<Restaurante> restaurantes = restauranteService.buscaRestauranteDisponiveis();
        return ResponseEntity.ok(restaurantes);
        
    }

    // GET /api/restaurantes/categoria/{categoria} - Listar por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Restaurante>> listarPorCategoria(@PathVariable String categoria) {
        List<Restaurante> restaurantes = restauranteService.buscaRestaurantePorCategoria(categoria);
        return ResponseEntity.ok(restaurantes);
    }

    // PUT /api/restaurantes/{id} - Atualizar restaurante
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponse> atualizar(@PathVariable Long id, @RequestBody RestauranteRequest request) {
        Restaurante restauranteAtualizado = restauranteService.atualizarRestaurante(id, request);
        return ResponseEntity.ok(mapper.toResponse(restauranteAtualizado));
    }

    // GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa de entrega
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxaEntrega(@PathVariable Long id, @PathVariable String cep) {
        BigDecimal taxaEntrega = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(taxaEntrega);
    }
    
}