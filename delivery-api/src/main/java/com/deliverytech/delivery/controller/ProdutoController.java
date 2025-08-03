package com.deliverytech.delivery.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery.dto.request.ProdutoRequest;
import com.deliverytech.delivery.dto.response.ProdutoResponse;
import com.deliverytech.delivery.dto.response.common.ApiWrapperResponse;
import com.deliverytech.delivery.dto.response.common.UtilsResponse;
import com.deliverytech.delivery.service.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @Operation(summary = "Cadastrar um novo produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping
    public ResponseEntity<ApiWrapperResponse<ProdutoResponse>> cadastrar(
        @Parameter(description = "Dados do produto para cadastro", required = true)
        @Valid @RequestBody ProdutoRequest request) {

        ProdutoResponse response = produtoService.cadastrarProduto(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UtilsResponse.created(response));
    }

    @Operation(summary = "Buscar produto por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiWrapperResponse<ProdutoResponse>> buscarPorId(
        @Parameter(description = "ID do produto a ser consultado", example = "1", in = ParameterIn.PATH, required = true)
        @PathVariable Long id) {

        ProdutoResponse response = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(UtilsResponse.success(response));
    }

    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
    @Operation(summary = "Atualizar um produto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiWrapperResponse<ProdutoResponse>> atualizar(
        @Parameter(description = "ID do produto a ser atualizado", example = "1", in = ParameterIn.PATH, required = true)
        @PathVariable Long id,

        @Parameter(description = "Dados para atualização do produto", required = true)
        @Valid @RequestBody ProdutoRequest request) {

        ProdutoResponse response = produtoService.atualizarProduto(id, request);
        return ResponseEntity.ok(UtilsResponse.success(response));
    }

    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
    @Operation(summary = "Remover produto por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
        @Parameter(description = "ID do produto a ser removido", example = "1", in = ParameterIn.PATH, required = true)
        @PathVariable Long id) {

        produtoService.removerProduto(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or @produtoServiceImpl.isOwner(#id)")
    @Operation(summary = "Alterar disponibilidade de um produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidade alterada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<ApiWrapperResponse<ProdutoResponse>> alterarDisponibilidade(
        @Parameter(description = "ID do produto", example = "1", in = ParameterIn.PATH, required = true)
        @PathVariable Long id,

        @Parameter(description = "Disponibilidade do produto", example = "true", in = ParameterIn.QUERY, required = true)
        @RequestParam boolean disponivel) {

        ProdutoResponse response = produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.ok(UtilsResponse.success(response));
    }

    @Operation(summary = "Listar produtos por categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produtos encontrados para a categoria"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada ou sem produtos")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiWrapperResponse<List<ProdutoResponse>>> listarPorCategoria(
        @Parameter(description = "Nome da categoria", example = "Lanches", in = ParameterIn.PATH, required = true)
        @PathVariable String categoria) {

        List<ProdutoResponse> produtos = produtoService.buscarProdutosPorCategoria(categoria);
        return ResponseEntity.ok(UtilsResponse.success(produtos));
    }

    @Operation(summary = "Buscar produtos pelo nome")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produtos encontrados"),
        @ApiResponse(responseCode = "404", description = "Nenhum produto encontrado com esse nome")
    })
    @GetMapping("/buscar")
    public ResponseEntity<ApiWrapperResponse<List<ProdutoResponse>>> buscarPorNome(
        @Parameter(description = "Nome do produto para busca", example = "X-Burguer", in = ParameterIn.QUERY, required = true)
        @RequestParam String nome) {

        List<ProdutoResponse> produtos = produtoService.buscarProdutosPorNome(nome);
        return ResponseEntity.ok(UtilsResponse.success(produtos));
    }

    @Operation(summary = "Listar produtos de um restaurante específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produtos encontrados para o restaurante"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado ou sem produtos")
    })
    @GetMapping("/restaurantes/{restauranteId}")
    public ResponseEntity<ApiWrapperResponse<List<ProdutoResponse>>> listarPorRestaurante(
        @Parameter(description = "ID do restaurante", example = "10", in = ParameterIn.PATH, required = true)
        @PathVariable Long restauranteId) {

        List<ProdutoResponse> produtos = produtoService.buscarProdutosPorRestaurante(restauranteId);
        return ResponseEntity.ok(UtilsResponse.success(produtos));
    }
}
