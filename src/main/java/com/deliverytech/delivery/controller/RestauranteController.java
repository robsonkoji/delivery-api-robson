package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final RestauranteRepository restauranteRepository;
    public RestauranteController(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    @GetMapping
    public List<Restaurante> listarTodos() {
        return restauranteRepository.findAll();
    }

    @PostMapping
    public Restaurante criar(@RequestBody Restaurante restaurante) {
        return restauranteRepository.save(restaurante);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Restaurante> buscarPorId(@PathVariable Long id) {
        return restauranteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}