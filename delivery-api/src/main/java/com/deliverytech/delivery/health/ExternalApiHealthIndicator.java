package com.deliverytech.delivery.health;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Component("externalApi")
public class ExternalApiHealthIndicator implements HealthIndicator {

    private final HttpClient client;

    // Construtor usado nos testes
    @Autowired
    public ExternalApiHealthIndicator(HttpClient client) {
        this.client = client;
    }

    // Construtor padrão usado pela aplicação
    public ExternalApiHealthIndicator() {
        this(HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build());
    }

    @Override
    public Health health() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();

        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            int status = response.statusCode();

            if (status == 200) {
                return Health.up().withDetail("externalApi", "OK").build();
            } else {
                return Health.down().withDetail("externalApi", "HTTP Status: " + status).build();
            }
        } catch (IOException e) {
            return Health.down(e).withDetail("externalApi", "Falha de conexão").build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Health.down(e).withDetail("externalApi", "Thread interrompida").build();
        }
    }
}
