package com.deliverytech.delivery.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class AuditLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuditLoggingInterceptor.class);

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws IOException {

        // Correlation ID: tenta pegar do header, senão gera um novo
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        MDC.put("timestamp", Instant.now().toString());

        logger.info("Incoming HTTP request: method={}, uri={}, correlationId={}",
                request.getMethod(),
                request.getRequestURI(),
                correlationId);

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                @Nullable Exception ex) {

        String correlationId = MDC.get("correlationId");
        if (correlationId == null) {
            correlationId = "N/A";
        }

        logger.info("HTTP response: status={}, correlationId={}",
                response.getStatus(),
                correlationId);

        if (ex != null) {
            logger.error("Error during request processing, correlationId={}", correlationId, ex);
        }

        MDC.clear();
    }

    // Métodos auxiliares para logging específico de auditoria

    public void logAutenticacao(String usuarioEmail, boolean sucesso, String correlationId) {
        logger.info("Autenticação {} para usuário {}, correlationId={}",
                sucesso ? "bem sucedida" : "falhou",
                usuarioEmail,
                correlationId);
    }

    public void logLogout(String usuarioEmail, String correlationId) {
        logger.info("Logout para usuário {}, correlationId={}", usuarioEmail, correlationId);
    }

    public void logCrudPedido(String operacao, Long pedidoId, String detalhes, String correlationId) {
        logger.info("Operação CRUD Pedido: operação={}, pedidoId={}, detalhes={}, correlationId={}",
                operacao, pedidoId, detalhes, correlationId);
    }

    public void logMudancaStatusPedido(Long pedidoId, String statusAnterior, String novoStatus, String correlationId) {
        logger.info("Mudança de status do Pedido: pedidoId={}, statusAnterior={}, novoStatus={}, correlationId={}",
                pedidoId, statusAnterior, novoStatus, correlationId);
    }

}
