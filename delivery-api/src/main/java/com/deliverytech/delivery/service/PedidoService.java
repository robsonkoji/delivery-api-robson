package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery.dto.request.PedidoRequest;
import com.deliverytech.delivery.dto.response.PedidoResponse;
import com.deliverytech.delivery.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoService {

    PedidoResponse criarPedido(PedidoRequest request);

    PedidoResponse buscarPedidoPorId(Long id);

    List<PedidoResponse> buscarPedidosPorCliente(Long clienteId);

    PedidoResponse atualizarStatusPedido(Long id, StatusPedido status);

    BigDecimal calcularTotalPedido(List<ItemPedidoRequest> itensRequest);

    void cancelarPedido(Long id);

    List<PedidoResponse> buscarPedidosPorRestaurante(Long restauranteId);

    List<PedidoResponse> listarPedidosComFiltro(StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim);

    boolean canAccess(Long pedidoId);
}
