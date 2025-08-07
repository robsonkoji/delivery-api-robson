package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery.dto.request.PedidoRequest;
import com.deliverytech.delivery.dto.response.PedidoResponse;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.dto.response.common.ApiWrapperResponse;
import com.deliverytech.delivery.dto.response.common.UtilsResponse;
import com.deliverytech.delivery.service.PedidoService;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @PreAuthorize("hasRole('CLIENTE')")
    @Timed(value = "pedido.criar.tempo", description = "Tempo para criar pedido")
    @PostMapping
    @Operation(summary = "Criar um novo pedido",
            responses = {
                @ApiResponse(responseCode = "200", description = "Pedido criado com sucesso"),
                @ApiResponse(responseCode = "400", description = "Dados inválidos")
            })
    public ResponseEntity<ApiWrapperResponse<PedidoResponse>> criarPedido(@Valid @RequestBody PedidoRequest request) {
        PedidoResponse response = pedidoService.criarPedido(request);
        return ResponseEntity.ok(UtilsResponse.created(response));
    }

    // Apenas ADMIN, RESTAURANTE dono do pedido ou CLIENTE dono do pedido pode acessar
    @PreAuthorize("hasRole('ADMIN') or " +
                  "(hasRole('CLIENTE') and @pedidoSecurity.isPedidoDoCliente(#id, principal.id)) or " +
                  "(hasRole('RESTAURANTE') and @pedidoSecurity.isPedidoDoRestaurante(#id, principal.id))")
    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID",
            parameters = {
                @Parameter(name = "id", description = "ID do pedido", required = true, in = ParameterIn.PATH, example = "1")
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
                @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
            })
    public ResponseEntity<ApiWrapperResponse<PedidoResponse>> buscarPorId(@PathVariable Long id) {
        PedidoResponse response = pedidoService.buscarPedidoPorId(id);
        return ResponseEntity.ok(UtilsResponse.success(response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Listar pedidos com filtros de status e data",
            parameters = {
                @Parameter(name = "status", description = "Status do pedido (ex: CRIADO, CANCELADO, ENTREGUE)", in = ParameterIn.QUERY),
                @Parameter(name = "dataInicio", description = "Data e hora inicial no formato dd-MM-yyyy HH:mm", in = ParameterIn.QUERY),
                @Parameter(name = "dataFim", description = "Data e hora final no formato dd-MM-yyyy HH:mm", in = ParameterIn.QUERY)
            })
    public ResponseEntity<ApiWrapperResponse<List<PedidoResponse>>> listarComFiltros(
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime inicio = null;
        LocalDateTime fim = null;

        try {
            if (dataInicio != null && !dataInicio.isBlank()) {
                inicio = LocalDateTime.parse(dataInicio, formatter);
            }
            if (dataFim != null && !dataFim.isBlank()) {
                fim = LocalDateTime.parse(dataFim, formatter);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use: dd-MM-yyyy HH:mm");
        }

        List<PedidoResponse> pedidos = pedidoService.listarPedidosComFiltro(status, inicio, fim);
        return ResponseEntity.ok(UtilsResponse.success(pedidos));
    }

    @PreAuthorize("hasRole('RESTAURANTE') and @pedidoSecurity.isPedidoDoRestaurante(#id, principal.id)")
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar o status do pedido")
    public ResponseEntity<ApiWrapperResponse<PedidoResponse>> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusPedido status
    ) {
        PedidoResponse response = pedidoService.atualizarStatusPedido(id, status);
        return ResponseEntity.ok(UtilsResponse.success(response));
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @pedidoSecurity.isPedidoDoCliente(#id, principal.id))")
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar um pedido por ID")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping("/calcular")
    @Operation(summary = "Calcular o total do pedido sem salvar")
    public ResponseEntity<ApiWrapperResponse<BigDecimal>> calcularTotal(@Valid @RequestBody List<ItemPedidoRequest> itens) {
        BigDecimal total = pedidoService.calcularTotalPedido(itens);
        return ResponseEntity.ok(UtilsResponse.success(total));
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #clienteId == principal.id)")
    @GetMapping("/clientes/{clienteId}")
    @Operation(summary = "Histórico de pedidos de um cliente")
    public ResponseEntity<ApiWrapperResponse<List<PedidoResponse>>> buscarPorCliente(@PathVariable Long clienteId) {
        List<PedidoResponse> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        return ResponseEntity.ok(UtilsResponse.success(pedidos));
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and #restauranteId == principal.id)")
    @GetMapping("/restaurantes/{restauranteId}")
    @Operation(summary = "Pedidos de um restaurante")
    public ResponseEntity<ApiWrapperResponse<List<PedidoResponse>>> buscarPorRestaurante(@PathVariable Long restauranteId) {
        List<PedidoResponse> pedidos = pedidoService.buscarPedidosPorRestaurante(restauranteId);
        return ResponseEntity.ok(UtilsResponse.success(pedidos));
    }
}
