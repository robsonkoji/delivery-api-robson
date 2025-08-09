package com.deliverytech.delivery.metrics;

import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.entity.Pedido;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class PedidoMetrics {

    private final PedidoRepository pedidoRepository;
    private final MeterRegistry meterRegistry;

    private Counter produtosVendidosCounter;
    private AtomicReference<Double> receitaUltimaHora;

    @PostConstruct
    public void init() {
        produtosVendidosCounter = meterRegistry.counter("produtos.vendidos.total");
        receitaUltimaHora = new AtomicReference<>(0.0);

        Gauge.builder("receita.por.hora", receitaUltimaHora, AtomicReference::get)
                .description("Receita total da última hora")
                .register(meterRegistry);
    }

    public void incrementarProdutosVendidos(int quantidade) {
        produtosVendidosCounter.increment(quantidade);
    }

    @Scheduled(fixedRate = 60 * 60 * 1000) // Atualiza a cada 1 hora
    public void atualizarReceitaUltimaHora() {
        LocalDateTime umaHoraAtras = LocalDateTime.now().minusHours(1);
        BigDecimal receita = pedidoRepository.findAll().stream()
                .filter(p -> p.getDataPedido().isAfter(umaHoraAtras))
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        receitaUltimaHora.set(receita.doubleValue());
    }

    public void incrementarPedidosPorStatus(String status) {
        meterRegistry.counter("pedidos.processados", "status", status).increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample, String nome, String acao) {
        sample.stop(Timer.builder(nome)
                .description("Tempo para " + acao)
                .tag("acao", acao)
                .register(meterRegistry));
    }
}
