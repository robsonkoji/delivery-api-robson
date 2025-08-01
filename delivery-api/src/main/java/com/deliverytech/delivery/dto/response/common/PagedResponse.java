package com.deliverytech.delivery.dto.response.common;


import java.time.Instant;
import java.util.List;


public record PagedResponse<T>(
        int status,
        String mensagem,
        List<T> dados,
        Instant timestamp,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}