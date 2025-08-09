package com.deliverytech.delivery.health;

import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component("compositeHealth")
public class CompositeHealthIndicator implements HealthIndicator {

    private final Map<String, HealthIndicator> indicators;

    public CompositeHealthIndicator(Map<String, HealthIndicator> indicators) {
        this.indicators = indicators;
    }

    @Override
    public Health health() {
        Health.Builder statusBuilder = Health.up();

        for (Map.Entry<String, HealthIndicator> entry : indicators.entrySet()) {
            Health health = entry.getValue().health();
            statusBuilder.withDetail(entry.getKey(), health.getStatus());

            if (health.getStatus().equals(org.springframework.boot.actuate.health.Status.DOWN)) {
                statusBuilder.status(Status.DOWN);
                statusBuilder.withDetails(health.getDetails());
            }
        }

        return statusBuilder.build();
    }
}
