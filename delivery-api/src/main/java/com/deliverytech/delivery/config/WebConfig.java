package com.deliverytech.delivery.config;

import com.deliverytech.delivery.logging.AuditLoggingInterceptor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuditLoggingInterceptor auditLoggingInterceptor;

    public WebConfig(AuditLoggingInterceptor auditLoggingInterceptor) {
        this.auditLoggingInterceptor = auditLoggingInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(auditLoggingInterceptor);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
        interceptors.add(new CorrelationIdRestTemplateInterceptor());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000") // Ajuste para sua origem real
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                        .allowedHeaders("*");
            }
        };
    }

    private static class CorrelationIdRestTemplateInterceptor implements ClientHttpRequestInterceptor {

        private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

        @Override
        public @NonNull ClientHttpResponse intercept(
                @NonNull HttpRequest request,
                @NonNull byte[] body,
                @NonNull ClientHttpRequestExecution execution
        ) throws IOException {
            String correlationId = MDC.get("correlationId"); // ajuste se necessário
            if (correlationId != null && !correlationId.isEmpty()) {
                request.getHeaders().set(CORRELATION_ID_HEADER, correlationId);
            }
            return execution.execute(request, body);
        }
    }
}
