package com.deliverytech.delivery.dto.response.common;

import java.time.Instant;

public record ApiWrapperResponse<T>(

        int status,
        String mensagem,
        T dados,
        Instant timestamp
) {}
