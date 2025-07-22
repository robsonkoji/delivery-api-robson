package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.model.Pedido;
import com.deliverytech.delivery.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    //pedidos de um cliente
    List<Pedido> findByClienteId(Long clienteId);

    //pedidos por status
    List<Pedido> findByStatus(StatusPedido status);

    //últimos 10 pedidos
    List<Pedido> findTop10ByOrderByDataPedidoDesc();

    //pedidos em um período
    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);
}