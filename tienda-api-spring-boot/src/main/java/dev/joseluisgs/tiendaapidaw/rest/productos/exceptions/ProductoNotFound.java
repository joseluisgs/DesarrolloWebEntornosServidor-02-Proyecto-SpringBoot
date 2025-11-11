package dev.joseluisgs.tiendaapidaw.rest.productos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

/**
 * Excepción de producto no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductoNotFound extends ProductoException {
    public ProductoNotFound(Long id) {
        super("Producto con id " + id + " no encontrado");
    }

    public ProductoNotFound(UUID uuid) {
        super("Producto con uuid " + uuid + " no encontrado");
    }

}
