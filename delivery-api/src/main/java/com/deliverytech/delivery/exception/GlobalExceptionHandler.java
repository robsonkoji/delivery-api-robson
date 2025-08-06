package com.deliverytech.delivery.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata erro de entidade não encontrada
    @ExceptionHandler(com.deliverytech.delivery.exception.EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(com.deliverytech.delivery.exception.EntityNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(
            getMessageOrDefault(ex, "Recurso não encontrado"),
            HttpStatus.NOT_FOUND,
            request.getRequestURI(),
            null
        );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        return buildErrorResponse(
            getMessageOrDefault(ex, "Erro de regra de negócio"),
            HttpStatus.UNPROCESSABLE_ENTITY,
            request.getRequestURI(),
            null
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex, HttpServletRequest request) {
        return buildErrorResponse(
            getMessageOrDefault(ex, "Conflito de dados"),
            HttpStatus.CONFLICT,
            request.getRequestURI(),
            null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::formatFieldError)
            .toList();

        return buildErrorResponse(
            "Violação de validação de campos",
            HttpStatus.UNPROCESSABLE_ENTITY,
            request.getRequestURI(),
            details
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestBody(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Erro de corpo de requisição malformado", ex);
        return buildErrorResponse(
            "Requisição malformada. Verifique o corpo da requisição.",
            HttpStatus.BAD_REQUEST,
            request.getRequestURI(),
            List.of(getMessageOrDefault(ex, "JSON inválido ou estrutura não reconhecida."))
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> details = ex.getConstraintViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .toList();

        return buildErrorResponse(
            "Violação de restrição nos parâmetros",
            HttpStatus.BAD_REQUEST,
            request.getRequestURI(),
            details
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Erro interno inesperado", ex);
        return buildErrorResponse(
            "Erro interno no servidor. Contate o administrador do sistema.",
            HttpStatus.INTERNAL_SERVER_ERROR,
            request.getRequestURI(),
            List.of(ex.getClass().getSimpleName() + ": " + getMessageOrDefault(ex, "Erro não especificado"))
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, String path, List<String> details) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setPath(path);
        error.setDetails(details);
        return new ResponseEntity<>(error, status);
    }

    private String getMessageOrDefault(Exception ex, String defaultMessage) {
        return Optional.ofNullable(ex.getMessage()).orElse(defaultMessage);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
