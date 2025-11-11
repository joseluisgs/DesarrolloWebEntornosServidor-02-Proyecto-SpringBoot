package dev.joseluisgs.tiendaapidaw.rest.categorias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Excepción de producto no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoriaNotFound extends CategoriaException {
    public CategoriaNotFound(UUID id) {
        super("Categoría con id " + id + " no encontrada");
    }

    public CategoriaNotFound(String categoria) {
        super("Categoría " + categoria + " no encontrada");
    }
}
