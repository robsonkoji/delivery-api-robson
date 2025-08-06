package com.deliverytech.delivery.health;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("diskSpace")
public class DiskSpaceHealthIndicator implements HealthIndicator {

    private static final long THRESHOLD = 100L * 1024 * 1024; // 100 MB
    private final File root;

    @Autowired
    // Construtor padrão usado em produção
    public DiskSpaceHealthIndicator() {
        this.root = new File("/");
    }

    // Construtor adicional para injetar File no teste
    public DiskSpaceHealthIndicator(File root) {
        this.root = root;
    }

    @Override
    public Health health() {
        long freeSpace = root.getFreeSpace();

        if (freeSpace > THRESHOLD) {
            return Health.up()
                .withDetail("freeSpace", freeSpace)
                .build();
        } else {
            return Health.down()
                .withDetail("freeSpace", freeSpace)
                .withDetail("threshold", THRESHOLD)
                .build();
        }
    }
}
