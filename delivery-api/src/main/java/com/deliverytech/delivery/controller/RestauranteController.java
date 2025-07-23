package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.model.Produto;
import com.deliverytech.delivery.model.Restaurante;
import com.deliverytech.delivery.repository.RestauranteRepository;
import com.deliverytech.delivery.service.ProdutoService;
import com.deliverytech.delivery.service.RestauranteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoService produtoService;
    public RestauranteController(RestauranteRepository restauranteRepository, RestauranteService restauranteService, ProdutoService produtoService) {
        this.restauranteRepository = restauranteRepository;
        this.restauranteService = restauranteService;
        this.produtoService = produtoService;
    }
    /**
     * Listar todos os restaurantes ativos
     */
    @GetMapping
    public ResponseEntity<List<Restaurante>> listar() {
        List<Restaurante> restaurante = restauranteService.listarAtivos();
        return ResponseEntity.ok(restaurante);
    }

    /*
     * Buscar por categoria
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Restaurante>> buscarPorCategoria(@PathVariable String categoria) {
        List<Restaurante> restaurantes = restauranteService.buscarPorCategoria(categoria);
        if (restaurantes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(restaurantes);
    }

    /*
     * Buscar por taxa de entrega menor ou igual
     */
    @GetMapping("/taxaentrega")
    public ResponseEntity<List<Restaurante>> buscarPorTaxaEntregaMenorOuIgual(@RequestParam BigDecimal taxa) {
        List<Restaurante> restaurantes = restauranteService.buscarPorTaxaEntregaMenorOuIgual(taxa);
        if (restaurantes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(restaurantes);
    }

    /*
     * Buscar os 5 primeiros restaurantes por nome (ordem alfabética)
     */
    @GetMapping("/top5")
    public ResponseEntity<List<Restaurante>> buscarTop5PorNome() {
        List<Restaurante> restaurantes = restauranteService.buscarTop5PorNome();
        if (restaurantes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(restaurantes);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> inativar(@PathVariable Long id) {
        try {
      restauranteService.inativar(id);
            return ResponseEntity.ok().body("Restaurante inativado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor.");
        }
    }
    /*
     *  Reativar o restaurante pelo id
     */
    @PutMapping("{id}/ativar")
    public ResponseEntity<Object> reativar(@PathVariable Long id) {
        try {
            restauranteService.ativar(id);
            return ResponseEntity.ok("Restaurante reativado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno no servidor.");
        }
    }
    @GetMapping("/{restauranteId}/produtos")
    public List<Produto> listarPorRestaurante(@PathVariable Long restauranteId) {
        return produtoService.listarPorRestaurante(restauranteId);
    }
}