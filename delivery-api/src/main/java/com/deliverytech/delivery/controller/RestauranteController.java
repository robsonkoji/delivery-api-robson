package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.service.RestauranteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ResponseEntity<Restaurante> cadastrar(@RequestBody RestauranteRequest request) {
        Restaurante restaurante = restauranteService.cadastrarRestaurante(request);
        return ResponseEntity.ok(restaurante);
    }

    // GET /api/restaurantes/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Restaurante> buscarPorId(@PathVariable Long id) {
        Restaurante restaurante = restauranteService.buscRestaurantePorId(id);
        return ResponseEntity.ok(restaurante);
    }

    // GET /api/restaurantes - Listar disponíveis (com paginação e filtros opcionais)
    @GetMapping
    public ResponseEntity<List<Restaurante>> listarDisponiveis(@RequestBody RestauranteRequest request) {
        List<Restaurante> restaurantes = restauranteService.buscRestauranteDisponiveis();
        return ResponseEntity.ok(restaurantes);
        
    }

    // GET /api/restaurantes/categoria/{categoria} - Listar por categoria
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Restaurante>> listarPorCategoria(@PathVariable String categoria) {
        List<Restaurante> restaurantes = restauranteService.buscRestaurantePorCategoria(categoria);
        return ResponseEntity.ok(restaurantes);
    }

    // PUT /api/restaurantes/{id} - Atualizar restaurante
    @PutMapping("/{id}")
    public ResponseEntity<Restaurante> atualizar(@PathVariable Long id, @RequestBody RestauranteRequest request) {
        Restaurante restaurante = restauranteService.atualizarRestaurante(id, request);
        return ResponseEntity.ok(restaurante);
    }

    // GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa de entrega
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxa(@PathVariable Long id, @PathVariable String cep) {
        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(taxa);
    }
    
}