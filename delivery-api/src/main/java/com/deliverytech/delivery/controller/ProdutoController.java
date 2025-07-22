package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.model.Produto;
import com.deliverytech.delivery.repository.ProdutoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {
    
    private final ProdutoRepository produtoRepository;
    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /*
     * Produtos de um restaurante
     */
    @GetMapping("/restaurante/{restauranteId}")
    public List<Produto> listarPorRestaurante(@PathVariable Long restauranteId) {
        return produtoRepository.findByRestauranteId(restauranteId);
    }

    /*
     * Listar todos os produtos
     */
    @GetMapping
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    /*
     * Produtos disponíveis
     */
    @GetMapping("/disponiveis")
    public List<Produto> listarDisponiveis() {
        return produtoRepository.findByDisponivelTrue();
    }

    /*
     * Buscar produtos por categoria
     */
    @GetMapping("/categoria/{categoria}")
    public List<Produto> buscarPorCategoria(@PathVariable String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    /*
     * produtos mais baratos que um valor
     */
    @GetMapping("/preco/menos-que/{preco}")
    public List<Produto> buscarPorPrecoMenorQue(@PathVariable BigDecimal preco) {
        return produtoRepository.findByPrecoLessThanEqual(preco);
    }

    @PostMapping
    public Produto criar(@RequestBody Produto produto) {
        return produtoRepository.save(produto);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
}
