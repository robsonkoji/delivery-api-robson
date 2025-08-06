package com.deliverytech.delivery.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AggregatedHealthIndicatorTest {

    private CustomDbHealthIndicator dbHealthIndicator;
    private ExternalApiHealthIndicator externalApiHealthIndicator;
    private AggregatedHealthIndicator aggregatedHealthIndicator;

    @BeforeEach
    void setup() {
        dbHealthIndicator = mock(CustomDbHealthIndicator.class);
        externalApiHealthIndicator = mock(ExternalApiHealthIndicator.class);
        aggregatedHealthIndicator = new AggregatedHealthIndicator(dbHealthIndicator, externalApiHealthIndicator);
    }

    @Test
    void shouldReturnUpWhenBothIndicatorsAreUp() {
        Health dbHealth = Health.up().withDetail("database", "Conexão válida").build();
        Health externalHealth = Health.up().withDetail("externalApi", "OK").build();

        when(dbHealthIndicator.health()).thenReturn(dbHealth);
        when(externalApiHealthIndicator.health()).thenReturn(externalHealth);

        Health result = aggregatedHealthIndicator.health();

        assertEquals(Status.UP, result.getStatus());
        assertTrue(result.getDetails().containsKey("database"));
        assertTrue(result.getDetails().containsKey("externalApi"));
        assertEquals("Conexão válida", result.getDetails().get("database"));
        assertEquals("OK", result.getDetails().get("externalApi"));
    }

    @Test
    void shouldReturnDownWhenAnyIndicatorIsDown() {
        Health dbHealth = Health.up().withDetail("database", "Conexão válida").build();
        Health externalHealth = Health.down().withDetail("externalApi", "Falha").build();

        when(dbHealthIndicator.health()).thenReturn(dbHealth);
        when(externalApiHealthIndicator.health()).thenReturn(externalHealth);

        Health result = aggregatedHealthIndicator.health();

        assertEquals(Status.DOWN, result.getStatus());
        assertTrue(result.getDetails().containsKey("database"));
        assertTrue(result.getDetails().containsKey("externalApi"));
        assertEquals("Conexão válida", result.getDetails().get("database"));
        assertEquals("Falha", result.getDetails().get("externalApi"));
    }
}
