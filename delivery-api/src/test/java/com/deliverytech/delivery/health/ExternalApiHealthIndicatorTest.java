package com.deliverytech.delivery.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.actuate.health.Health;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExternalApiHealthIndicatorTest {

    private HttpClient mockClient;
    private HttpResponse<Void> mockResponse;
    private ExternalApiHealthIndicator indicator;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
        mockClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        indicator = new ExternalApiHealthIndicator(mockClient);
    }

    @Test
    void shouldReturnUpWhenResponseIs200() throws Exception {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockClient.send(
                any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any()
        )).thenReturn(mockResponse);

        Health health = indicator.health();

        assertEquals("UP", health.getStatus().getCode());
        assertEquals("OK", health.getDetails().get("externalApi"));
    }

    @Test
    void shouldReturnDownWhenResponseIsNot200() throws Exception {
        when(mockResponse.statusCode()).thenReturn(503);
        when(mockClient.send(
                any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any()
        )).thenReturn(mockResponse);

        Health health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals("HTTP Status: 503", health.getDetails().get("externalApi"));
    }

    @Test
    void shouldReturnDownWhenIOExceptionIsThrown() throws Exception {
        when(mockClient.send(
                any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any()
        )).thenThrow(new IOException("Erro de rede"));

        Health health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals("Falha de conexão", health.getDetails().get("externalApi"));
    }

    @Test
    void shouldReturnDownWhenInterruptedExceptionIsThrown() throws Exception {
        when(mockClient.send(
                any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<Void>>any()
        )).thenThrow(new InterruptedException("Interrompido"));

        Health health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals("Thread interrompida", health.getDetails().get("externalApi"));
    }
}
