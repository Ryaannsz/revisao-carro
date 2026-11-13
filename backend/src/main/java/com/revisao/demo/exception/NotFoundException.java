package com.revisao.demo.exception;

/**
 * Registro inexistente ou pertencente a outra empresa.
 * Responde 404 nos dois casos para não revelar que o recurso existe em outro tenant.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
