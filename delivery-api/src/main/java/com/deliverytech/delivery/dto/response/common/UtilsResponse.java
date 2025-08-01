package com.deliverytech.delivery.dto.response.common;

import java.time.Instant;
import java.util.List;

public final class UtilsResponse {

    private  UtilsResponse(){}

    public static <T> ApiWrapperResponse<T> success(T data) {
        return new ApiWrapperResponse<>(200, "Sucesso", data, Instant.now());
    }

    public static <T> ApiWrapperResponse<T> created(T data) {
        return new ApiWrapperResponse<>(201, "Criado com sucesso", data, Instant.now());
    }

    public static ApiWrapperResponse<Void> noContent() {
        return new ApiWrapperResponse<>(204, "Sem conteúdo", null, Instant.now());
    }

    public static <T> PagedResponse<T> pagedSuccess(List<T> data, int page, int size, long totalElements, int totalPages) {
        return new PagedResponse<>(200, "Sucesso", data, Instant.now(), page, size, totalElements, totalPages);
    }
}
