package com.deliverytech.delivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.deliverytech.delivery.repository.ClienteRepository;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MetricsConfig {

    @Bean
    public Gauge usuariosAtivosGauge(ClienteRepository clienteRepository, MeterRegistry registry) {
        return Gauge.builder("usuarios.ativos", clienteRepository, ClienteRepository::count)
                    .description("Número de usuários ativos")
                    .register(registry);
    }
}
