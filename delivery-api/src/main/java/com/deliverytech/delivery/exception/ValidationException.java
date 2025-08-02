package com.deliverytech.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando há falhas de validação de dados de entrada.
 * Por exemplo: campos obrigatórios não informados, formatos inválidos etc.
 * Esta exceção herda de BusinessException e indica erro 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class ValidationException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem explicativa da falha de validação.
     *
     * @param message Descrição da violação de validação.
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Descrição da violação de validação.
     * @param cause   Exceção original que causou esta exceção.
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
