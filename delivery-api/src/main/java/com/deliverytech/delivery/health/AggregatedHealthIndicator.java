package com.deliverytech.delivery.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("healthAggregator")
public class AggregatedHealthIndicator implements HealthIndicator {

    private final CustomDbHealthIndicator dbHealthIndicator;
    private final ExternalApiHealthIndicator externalApiHealthIndicator;

    public AggregatedHealthIndicator(CustomDbHealthIndicator dbHealthIndicator,
                                     ExternalApiHealthIndicator externalApiHealthIndicator) {
        this.dbHealthIndicator = dbHealthIndicator;
        this.externalApiHealthIndicator = externalApiHealthIndicator;
    }

    @Override
    public Health health() {
        Health dbHealth = dbHealthIndicator.health();
        Health externalHealth = externalApiHealthIndicator.health();

        if (dbHealth.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)
            && externalHealth.getStatus().equals(org.springframework.boot.actuate.health.Status.UP)) {
            return Health.up()
                    .withDetails(dbHealth.getDetails())
                    .withDetails(externalHealth.getDetails())
                    .build();
        } else {
            return Health.down()
                    .withDetails(dbHealth.getDetails())
                    .withDetails(externalHealth.getDetails())
                    .build();
        }
    }
}
