package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.response.*;
import com.deliverytech.delivery.service.RelatorioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @Operation(
        summary = "Relatório de vendas por restaurante",
        description = "Retorna uma lista de vendas agrupadas por restaurante",
        responses = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = RelatorioVendaPorRestauranteResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @GetMapping("/vendas-por-restaurante")
    public ResponseEntity<List<RelatorioVendaPorRestauranteResponse>> vendasPorRestaurante() {
        return ResponseEntity.ok(relatorioService.obterVendasPorRestaurante());
    }

    @Operation(
        summary = "Produtos mais vendidos",
        description = "Retorna os produtos mais vendidos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = RelatorioProdutoMaisVendidoResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<RelatorioProdutoMaisVendidoResponse>> produtosMaisVendidos() {
        return ResponseEntity.ok(relatorioService.obterProdutosMaisVendidos());
    }

    @Operation(
        summary = "Clientes mais ativos",
        description = "Retorna os clientes com maior número de pedidos",
        responses = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = RelatorioClienteAtivoResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @GetMapping("/clientes-ativos")
    public ResponseEntity<List<RelatorioClienteAtivoResponse>> clientesMaisAtivos() {
        return ResponseEntity.ok(relatorioService.obterClientesMaisAtivos());
    }

    @Operation(
        summary = "Pedidos por período",
        description = "Retorna um resumo dos pedidos feitos entre duas datas",
        responses = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = RelatorioPedidosPorPeriodoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de data inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
        }
    )
    @GetMapping("/pedidos-por-periodo")
    public ResponseEntity<RelatorioPedidosPorPeriodoResponse> pedidosPorPeriodo(
            @Parameter(description = "Data de início no formato yyyy-MM-dd", example = "2024-01-01")
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @Parameter(description = "Data de fim no formato yyyy-MM-dd", example = "2024-12-31")
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        return ResponseEntity.ok(relatorioService.obterPedidosPorPeriodo(dataInicio, dataFim));
    }
}
