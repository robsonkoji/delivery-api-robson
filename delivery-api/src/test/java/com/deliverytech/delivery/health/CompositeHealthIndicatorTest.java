package com.deliverytech.delivery.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompositeHealthIndicatorTest {

    private HealthIndicator dbHealthIndicator;
    private HealthIndicator externalApiHealthIndicator;
    private CompositeHealthIndicator compositeHealthIndicator;

    @BeforeEach
    void setUp() {
        dbHealthIndicator = mock(HealthIndicator.class);
        externalApiHealthIndicator = mock(HealthIndicator.class);

        Map<String, HealthIndicator> indicators = new HashMap<>();
        indicators.put("customDbHealth", dbHealthIndicator);
        indicators.put("externalApi", externalApiHealthIndicator);

        compositeHealthIndicator = new CompositeHealthIndicator(indicators);
    }

    @Test
    void shouldReturnUpWhenAllIndicatorsAreUp() {
        when(dbHealthIndicator.health()).thenReturn(Health.up().build());
        when(externalApiHealthIndicator.health()).thenReturn(Health.up().build());

        Health result = compositeHealthIndicator.health();

        assertEquals(Status.UP, result.getStatus());
        assertEquals(Status.UP, result.getDetails().get("customDbHealth"));
        assertEquals(Status.UP, result.getDetails().get("externalApi"));
    }

    @Test
    void shouldReturnDownWhenAnyIndicatorIsDown() {
        when(dbHealthIndicator.health()).thenReturn(Health.up().build());
        when(externalApiHealthIndicator.health()).thenReturn(
                Health.down().withDetail("error", "Service unavailable").build()
        );

        Health result = compositeHealthIndicator.health();

        assertEquals(Status.DOWN, result.getStatus());
        assertEquals(Status.UP, result.getDetails().get("customDbHealth"));
        assertEquals(Status.DOWN, result.getDetails().get("externalApi"));

        // Também verifica se os detalhes do indicador DOWN foram agregados
        assertTrue(result.getDetails().values().stream().anyMatch(detail -> detail.equals(Status.DOWN) || (detail instanceof Map && ((Map<?, ?>) detail).containsValue("Service unavailable"))));
    }
}
