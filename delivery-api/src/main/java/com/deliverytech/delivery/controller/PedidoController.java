package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.model.Pedido;
import com.deliverytech.delivery.service.PedidoService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /*
     * Pedidos de um cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> buscarPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.buscarPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }
    /**
     * Pedidos por status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Pedido>> buscarPorStatus(@PathVariable StatusPedido status) {
        List<Pedido> pedidos = pedidoService.buscarPorStatus(status);
        return ResponseEntity.ok(pedidos);
    }
    /**
     * Últimos 10 pedidos
     */
    @GetMapping("/ultimos")
    public ResponseEntity<List<Pedido>> buscarUltimosPedidos() {
        List<Pedido> pedidos = pedidoService.buscarUltimos10Pedidos();
        return ResponseEntity.ok(pedidos);
    }
    /**
     * Pedidos em um período
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<Pedido>> buscarPorPeriodo(
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy''HH:mm") LocalDateTime inicio,
        @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy''HH:mm") LocalDateTime fim) {
        List<Pedido> pedidos = pedidoService.buscarPorPeriodo(inicio, fim);
        return ResponseEntity.ok(pedidos);
    }


    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedido) {
        Pedido novoPedido = pedidoService.criar(pedido);
        return ResponseEntity.ok(novoPedido);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Pedido> alterarStatus(@PathVariable Long id, @RequestParam StatusPedido status) {
        Pedido pedidoAtualizado = pedidoService.alterarStatus(id, status);
        return ResponseEntity.ok(pedidoAtualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);
        return pedido.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
}
