package dev.joseluisgs.tiendaapidaw.rest.categorias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de producto no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CategoriaConflict extends CategoriaException {

    public CategoriaConflict(String message) {
        super(message);
    }
}
