package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface RelatorioService {

    List<RelatorioVendaPorRestauranteResponse> obterVendasPorRestaurante();

    List<RelatorioProdutoMaisVendidoResponse> obterProdutosMaisVendidos();

    List<RelatorioClienteAtivoResponse> obterClientesMaisAtivos();

    RelatorioPedidosPorPeriodoResponse obterPedidosPorPeriodo(LocalDate dataInicio, LocalDate dataFim);
}
