package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.dto.response.RelatorioClienteAtivoResponse;
import com.deliverytech.delivery.dto.response.RelatorioProdutoMaisVendidoResponse;
import com.deliverytech.delivery.dto.response.RelatorioVendaPorRestauranteResponse;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.enums.StatusPedido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByRestauranteId(Long restauranteId);

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Pedido> findByStatusAndDataPedidoBetween(StatusPedido status, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
        SELECT new com.deliverytech.delivery.dto.response.RelatorioVendaPorRestauranteResponse(
            p.restaurante.id,
            p.restaurante.nome,
            SUM(p.valorTotal),
            COUNT(p.id)
        )
        FROM Pedido p
        WHERE p.status = 'ENTREGUE'
        GROUP BY p.restaurante.id, p.restaurante.nome
        ORDER BY SUM(p.valorTotal) DESC
    """)
    List<RelatorioVendaPorRestauranteResponse> buscarVendasPorRestaurante();

    @Query("""
        SELECT new com.deliverytech.delivery.dto.response.RelatorioProdutoMaisVendidoResponse(
            ip.produto.id,
            ip.produto.nome,
            SUM(ip.quantidade)
        )
        FROM Pedido p
        JOIN p.itens ip
        WHERE p.status = 'ENTREGUE'
        GROUP BY ip.produto.id, ip.produto.nome
        ORDER BY SUM(ip.quantidade) DESC
    """)
    List<RelatorioProdutoMaisVendidoResponse> buscarProdutosMaisVendidos();

    @Query("""
        SELECT new com.deliverytech.delivery.dto.response.RelatorioClienteAtivoResponse(
            p.cliente.id,
            p.cliente.nome,
            COUNT(p.id)
        )
        FROM Pedido p
        WHERE p.status = 'ENTREGUE'
        GROUP BY p.cliente.id, p.cliente.nome
        ORDER BY COUNT(p.id) DESC
    """)
    List<RelatorioClienteAtivoResponse> buscarClientesMaisAtivos();

    @Query("""
        SELECT COUNT(p), COALESCE(SUM(p.valorTotal), 0)
        FROM Pedido p
        WHERE p.status = 'ENTREGUE' AND p.dataPedido BETWEEN :inicio AND :fim
    """)
    Object[] buscarPedidosPorPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
