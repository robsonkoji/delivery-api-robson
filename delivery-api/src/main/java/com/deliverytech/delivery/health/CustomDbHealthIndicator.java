package com.deliverytech.delivery.health;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("customDbHealth")
public class CustomDbHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public CustomDbHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return Health.up().withDetail("database", "Conexão válida").build();
            } else {
                return Health.down().withDetail("database", "Conexão inválida").build();
            }
        } catch (SQLException e) {
            return Health.down(e).build();
        }
    }
}
