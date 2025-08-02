package com.deliverytech.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção genérica para erros de negócio (validações, regras do domínio, etc).
 * Deve ser lançada quando uma regra de negócio for violada.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem de erro.
     * 
     * @param message Mensagem explicando a violação da regra de negócio.
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem de erro e causa raiz.
     *
     * @param message Mensagem explicando a violação da regra de negócio.
     * @param cause   Exceção original que causou esse erro.
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
