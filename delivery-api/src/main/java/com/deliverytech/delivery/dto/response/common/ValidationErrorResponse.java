package com.deliverytech.delivery.dto.response.common;

import java.time.LocalDateTime;
import java.util.List;

public class ValidationErrorResponse {
    private int status;
    private List<String> errors;
    private LocalDateTime timestamp;

    public ValidationErrorResponse() {}

    public ValidationErrorResponse(int status, List<String> errors) {
        this.status = status;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    // getters e setters
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public List<String> getErrors() {
        return errors;
    }
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
