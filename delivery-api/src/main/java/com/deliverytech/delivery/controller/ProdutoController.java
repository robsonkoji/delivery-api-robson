package com.deliverytech.delivery.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.service.ProdutoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {
    
    private final ProdutoService produtoService;

       // POST /api/produtos - Cadastrar produto
    @PostMapping("/produtos")
    public ResponseEntity<Produto> cadastrar(@RequestBody ProdutoRequest request) {
        Produto produto = produtoService.cadastrarProduto(request);
        return ResponseEntity.ok(produto);
    }

    // GET /api/produtos/{id} - Buscar por ID
    @GetMapping("/produtos/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    // GET /api/restaurantes/{restauranteId}/produtos - Produtos do restaurante
    @GetMapping("/restaurantes/{restauranteId}/produtos")
    public ResponseEntity<List<Produto>> listarPorRestaurante(@PathVariable Long restauranteId) {
        List<Produto> produtos = produtoService.buscarProdutosPorRestaurante(restauranteId);
        return ResponseEntity.ok(produtos);
    }

    // PUT /api/produtos/{id} - Atualizar produto
    @PutMapping("/produtos/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody ProdutoRequest request) {
        Produto produto = produtoService.atualizarProduto(id, request);
        return ResponseEntity.ok(produto);
    }

    // PATCH /api/produtos/{id}/disponibilidade - Alterar disponibilidade
    @PatchMapping("/produtos/{id}/disponibilidade")
    public ResponseEntity<Void> alterarDisponibilidade(@PathVariable Long id, @RequestParam boolean disponivel) {
        produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.noContent().build();
    }

    // GET /api/produtos/categoria/{categoria} - Listar produtos por categoria
    @GetMapping("/produtos/categoria/{categoria}")
    public ResponseEntity<List<Produto>> listarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarProdutosPorCategoria(categoria);
        return ResponseEntity.ok(produtos);

    }
}
