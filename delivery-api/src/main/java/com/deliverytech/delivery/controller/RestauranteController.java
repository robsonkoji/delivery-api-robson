package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.RestauranteRequest;
import com.deliverytech.delivery.dto.response.RestauranteResponse;
import com.deliverytech.delivery.dto.response.common.ApiWrapperResponse;
import com.deliverytech.delivery.dto.response.common.UtilsResponse;
import com.deliverytech.delivery.service.RestauranteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@Tag(name = "Restaurantes", description = "Operações relacionadas a restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar um novo restaurante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping
    public ResponseEntity<ApiWrapperResponse<RestauranteResponse>> cadastrar(
        @Parameter(description = "Dados do restaurante para cadastro", required = true)
        @Valid @RequestBody RestauranteRequest request) {

        RestauranteResponse restaurante = restauranteService.cadastrarRestaurante(request);
        URI location = URI.create("/api/restaurantes/" + restaurante.getId());
        return ResponseEntity.created(location)
                .body(UtilsResponse.created(restaurante));
    }

    @Operation(summary = "Buscar restaurante por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiWrapperResponse<RestauranteResponse>> buscarPorId(
        @Parameter(description = "ID do restaurante", example = "1", required = true) 
        @PathVariable Long id) {

        RestauranteResponse restaurante = restauranteService.buscarRestaurantePorId(id);
        return ResponseEntity.ok(UtilsResponse.success(restaurante));
    }

    @Operation(summary = "Listar restaurantes com filtros")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de restaurantes retornada")
    })
    @GetMapping
    public ResponseEntity<ApiWrapperResponse<List<RestauranteResponse>>> listar(
        @Parameter(description = "Categoria do restaurante", example = "Japonesa") 
        @RequestParam(required = false) String categoria,

        @Parameter(description = "Filtrar por status ativo (true ou false)", example = "true") 
        @RequestParam(required = false) Boolean ativo) {

        List<RestauranteResponse> restaurantes = restauranteService.buscarRestaurantesComFiltros(categoria, ativo);
        return ResponseEntity.ok(UtilsResponse.success(restaurantes));
    }

    @Operation(summary = "Listar restaurantes por categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de restaurantes retornada")
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<ApiWrapperResponse<List<RestauranteResponse>>> listarPorCategoria(
        @Parameter(description = "Nome da categoria", example = "Italiana", required = true) 
        @PathVariable String categoria) {

        List<RestauranteResponse> restaurantes = restauranteService.buscarRestaurantesPorCategoria(categoria);
        return ResponseEntity.ok(UtilsResponse.success(restaurantes));
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.isOwner(#id))")
    @Operation(summary = "Atualizar dados de um restaurante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiWrapperResponse<RestauranteResponse>> atualizar(
        @Parameter(description = "ID do restaurante a ser atualizado", example = "1", required = true) 
        @PathVariable Long id,

        @Parameter(description = "Dados para atualização do restaurante", required = true)
        @Valid @RequestBody RestauranteRequest request) {

        RestauranteResponse atualizado = restauranteService.atualizarRestaurante(id, request);
        return ResponseEntity.ok(UtilsResponse.success(atualizado));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Ativar ou desativar um restaurante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status do restaurante alterado"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiWrapperResponse<RestauranteResponse>> alterarStatus(
        @Parameter(description = "ID do restaurante", example = "1", required = true) 
        @PathVariable Long id) {

        RestauranteResponse response = restauranteService.alterarStatusRestaurante(id);
        return ResponseEntity.ok(UtilsResponse.success(response));
    }

    @Operation(summary = "Calcular taxa de entrega para um restaurante com base no CEP")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Taxa de entrega calculada"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado"),
        @ApiResponse(responseCode = "400", description = "CEP inválido")
    })
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<ApiWrapperResponse<BigDecimal>> calcularTaxaEntrega(
        @Parameter(description = "ID do restaurante", example = "1", required = true) 
        @PathVariable Long id,

        @Parameter(description = "CEP de destino", example = "01001000", required = true) 
        @PathVariable String cep) {

        BigDecimal taxaEntrega = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(UtilsResponse.success(taxaEntrega));
    }

    @Operation(summary = "Listar restaurantes próximos com base no CEP")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de restaurantes próximos retornada"),
        @ApiResponse(responseCode = "400", description = "CEP inválido")
    })
    @GetMapping("/proximos/{cep}")
    public ResponseEntity<ApiWrapperResponse<List<RestauranteResponse>>> listarProximos(
        @Parameter(description = "CEP de referência", example = "01310930", required = true) 
        @PathVariable String cep) {

        List<RestauranteResponse> proximos = restauranteService.buscarRestaurantesProximos(cep);
        return ResponseEntity.ok(UtilsResponse.success(proximos));
    }
}
