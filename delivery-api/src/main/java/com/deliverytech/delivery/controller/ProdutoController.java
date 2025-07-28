package com.deliverytech.delivery.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.dto.response.ProdutoResponse;
import com.deliverytech.delivery.service.ProdutoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {
    
    private final ProdutoService produtoService;

       // POST /api/produtos - Cadastrar produto
    @PostMapping("/produtos")
    public ResponseEntity<ProdutoResponse> cadastrar(@RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.cadastrarProduto(request));
    }

    // GET /api/produtos/{id} - Buscar por ID
    @GetMapping("/produtos/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarProdutoPorId(id));
    }

    // GET /api/restaurantes/{restauranteId}/produtos - Produtos do restaurante
    @GetMapping("/restaurantes/{restauranteId}/produtos")
    public ResponseEntity<List<ProdutoResponse>> listarPorRestaurante(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorRestaurante(restauranteId));
    }

    // PUT /api/produtos/{id} - Atualizar produto
    @PutMapping("/produtos/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id, @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.atualizarProduto(id, request));
    }

    // PATCH /api/produtos/{id}/disponibilidade - Alterar disponibilidade
    @PatchMapping("/produtos/{id}/disponibilidade")
    public ResponseEntity<Void> alterarDisponibilidade(@PathVariable Long id, @RequestParam boolean disponivel) {
        produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.noContent().build();
    }

    // GET /api/produtos/categoria/{categoria} - Listar produtos por categoria
    @GetMapping("/produtos/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponse>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorCategoria(categoria));

    }
}
