package com.deliverytech.delivery.service.impl;

import com.deliverytech.delivery.dto.response.*;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.service.RelatorioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.GlobalOpenTelemetry;

@Service
public class RelatorioServiceImpl implements RelatorioService {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioServiceImpl.class);

    private final PedidoRepository pedidoRepository;

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("delivery-api");

    public RelatorioServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    private String getCorrelationId() {
        String cid = MDC.get("correlationId");
        return cid != null ? cid : "N/A";
    }

    private void logError(String msg, Exception e) {
        logger.error("[{}] {}", getCorrelationId(), msg, e);
    }

    @Override
    public List<RelatorioVendaPorRestauranteResponse> obterVendasPorRestaurante() {
        Span span = tracer.spanBuilder("RelatorioServiceImpl.obterVendasPorRestaurante").startSpan();
        try {
            return pedidoRepository.buscarVendasPorRestaurante();
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logError("Erro ao obter vendas por restaurante", e);
            return Collections.emptyList();
        } finally {
            span.end();
        }
    }

    @Override
    public List<RelatorioProdutoMaisVendidoResponse> obterProdutosMaisVendidos() {
        Span span = tracer.spanBuilder("RelatorioServiceImpl.obterProdutosMaisVendidos").startSpan();
        try {
            return pedidoRepository.buscarProdutosMaisVendidos();
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logError("Erro ao obter produtos mais vendidos", e);
            return Collections.emptyList();
        } finally {
            span.end();
        }
    }

    @Override
    public List<RelatorioClienteAtivoResponse> obterClientesMaisAtivos() {
        Span span = tracer.spanBuilder("RelatorioServiceImpl.obterClientesMaisAtivos").startSpan();
        try {
            return pedidoRepository.buscarClientesMaisAtivos();
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logError("Erro ao obter clientes mais ativos", e);
            return Collections.emptyList();
        } finally {
            span.end();
        }
    }

    @Override
    public RelatorioPedidosPorPeriodoResponse obterPedidosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Span span = tracer.spanBuilder("RelatorioServiceImpl.obterPedidosPorPeriodo").startSpan();
        span.setAttribute("dataInicio", dataInicio.toString());
        span.setAttribute("dataFim", dataFim.toString());

        try {
            Object[] resultado = pedidoRepository.buscarPedidosPorPeriodo(dataInicio, dataFim);
            if (resultado == null || resultado.length < 2) {
                logger.warn("[{}] Resultado inválido ao buscar pedidos por período: dataInicio={}, dataFim={}",
                        getCorrelationId(), dataInicio, dataFim);
                return new RelatorioPedidosPorPeriodoResponse();
            }

            Long totalPedidos = (Long) resultado[0];
            BigDecimal valorTotal = (BigDecimal) resultado[1];

            RelatorioPedidosPorPeriodoResponse response = new RelatorioPedidosPorPeriodoResponse();
            response.setDataInicio(dataInicio.toString());
            response.setDataFim(dataFim.toString());
            response.setTotalPedidos(totalPedidos);
            response.setValorTotal(valorTotal);

            return response;
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            logError("Erro ao obter pedidos por período: dataInicio=" + dataInicio + ", dataFim=" + dataFim, e);
            return new RelatorioPedidosPorPeriodoResponse();
        } finally {
            span.end();
        }
    }
}
