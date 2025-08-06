package com.deliverytech.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exceção lançada quando uma entidade (ex: Cliente, Restaurante, Pedido) não é encontrada no sistema.
 * Essa exceção herda de BusinessException para manter a hierarquia de exceções de negócio.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public final class EntityNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construtor com mensagem.
     * 
     * @param message Mensagem descrevendo qual entidade não foi encontrada.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     *
     * @param message Mensagem descrevendo o erro.
     * @param cause   Exceção original que causou o erro.
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
