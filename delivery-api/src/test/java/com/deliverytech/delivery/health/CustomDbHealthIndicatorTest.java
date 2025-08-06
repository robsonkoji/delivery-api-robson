package com.deliverytech.delivery.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomDbHealthIndicatorTest {

    private DataSource dataSource;
    private Connection connection;
    private CustomDbHealthIndicator healthIndicator;

    @BeforeEach
    void setup() throws SQLException {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(connection);

        healthIndicator = new CustomDbHealthIndicator(dataSource);
    }

    @Test
    void shouldReturnUpWhenConnectionIsValid() throws SQLException {
        when(connection.isValid(2)).thenReturn(true);

        Health health = healthIndicator.health();

        assertEquals("UP", health.getStatus().getCode());
        assertEquals("Conexão válida", health.getDetails().get("database"));
        verify(connection).close();
    }

    @Test
    void shouldReturnDownWhenConnectionIsInvalid() throws SQLException {
        when(connection.isValid(2)).thenReturn(false);

        Health health = healthIndicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals("Conexão inválida", health.getDetails().get("database"));
    }

    @Test
    void shouldReturnDownWhenSQLExceptionIsThrown() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("Erro simulado"));

        Health health = healthIndicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertTrue(health.getDetails().get("error").toString().contains("Erro simulado"));
    }
}
