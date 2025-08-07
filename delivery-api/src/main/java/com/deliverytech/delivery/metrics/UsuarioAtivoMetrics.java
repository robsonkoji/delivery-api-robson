package com.deliverytech.delivery.metrics;


import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UsuarioAtivoMetrics {

    private final Set<String> usuariosAtivos = ConcurrentHashMap.newKeySet();

    public UsuarioAtivoMetrics(MeterRegistry registry) {
        Gauge.builder("usuarios.ativos", usuariosAtivos, Set::size)
            .description("Número de usuários ativos atualmente")
            .register(registry);
    }

    public void registrarLogin(String usuarioId) {
        usuariosAtivos.add(usuarioId);
    }

    public void registrarLogout(String usuarioId) {
        usuariosAtivos.remove(usuarioId);
    }

    public boolean isUsuarioAtivo(String usuarioId) {
        return usuariosAtivos.contains(usuarioId);
    }
}
