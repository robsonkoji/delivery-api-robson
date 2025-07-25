package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery.dto.request.PedidoRequest;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.enums.StatusPedido;

import java.math.BigDecimal;
import java.util.List;

public interface PedidoService {
    Pedido criarPedido(PedidoRequest request);
    Pedido buscarPedidoPorId(Long id);
    List<Pedido> buscarPedidosPorCliente(Long clienteId);
    Pedido atualizarStatusPedido(Long id, StatusPedido status);
    BigDecimal calcularTotalPedido(List<ItemPedidoRequest> itensRequest);
    void cancelarPedido(Long id);
}
