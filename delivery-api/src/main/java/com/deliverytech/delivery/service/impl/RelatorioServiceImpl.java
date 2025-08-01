package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.dto.response.*;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.service.RelatorioService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RelatorioServiceImpl implements RelatorioService {

    private final PedidoRepository pedidoRepository;

    public RelatorioServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public List<RelatorioVendaPorRestauranteResponse> obterVendasPorRestaurante() {
        return pedidoRepository.buscarVendasPorRestaurante();
    }

    @Override
    public List<RelatorioProdutoMaisVendidoResponse> obterProdutosMaisVendidos() {
        return pedidoRepository.buscarProdutosMaisVendidos();
    }

    @Override
    public List<RelatorioClienteAtivoResponse> obterClientesMaisAtivos() {
        return pedidoRepository.buscarClientesMaisAtivos();
    }

    @Override
    public RelatorioPedidosPorPeriodoResponse obterPedidosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Object[] resultado = pedidoRepository.buscarPedidosPorPeriodo(dataInicio, dataFim);

        Long totalPedidos = (Long) resultado[0];
        BigDecimal valorTotal = (BigDecimal) resultado[1];

        RelatorioPedidosPorPeriodoResponse response = new RelatorioPedidosPorPeriodoResponse();
        response.setDataInicio(dataInicio.toString());
        response.setDataFim(dataFim.toString());
        response.setTotalPedidos(totalPedidos);
        response.setValorTotal(valorTotal);

        return response;
    }
}
