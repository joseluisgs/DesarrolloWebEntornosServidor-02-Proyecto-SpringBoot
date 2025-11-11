package dev.joseluisgs.tiendaapidaw.rest.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de producto no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductoBadPrice extends PedidoException {
    public ProductoBadPrice(Long id) {
        super("Producto con id " + id + " no tiene un precio válido o no coincide con su precio actual");
    }
}
