package com.nttdata.bankapp.transaction.exception;

/**
 * Excepción personalizada para recurso no encontrado.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
