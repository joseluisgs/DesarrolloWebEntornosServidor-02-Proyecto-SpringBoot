package dev.joseluisgs.tiendaapidaw.rest.storage.exceptions;

import java.io.Serial;

// Nos permite devolver un estado cuando salta la excepción
public abstract class StorageException extends RuntimeException {
    // Por si debemos serializar
    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    public StorageException(String mensaje) {
        super(mensaje);
    }
}