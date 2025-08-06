package com.deliverytech.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Exceção lançada quando ocorre um conflito de dados,
 * como tentativa de criação de recurso duplicado ou violação de integridade.
 * Representa um erro HTTP 409 (Conflict).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public final class ConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem explicativa do conflito.
     *
     * @param message Mensagem explicando o motivo do conflito.
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa raiz do conflito.
     *
     * @param message Mensagem explicando o motivo do conflito.
     * @param cause   Exceção original que causou o conflito.
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
