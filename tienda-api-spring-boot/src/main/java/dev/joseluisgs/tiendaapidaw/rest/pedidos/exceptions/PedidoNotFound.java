package dev.joseluisgs.tiendaapidaw.rest.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de producto no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PedidoNotFound extends PedidoException {
    public PedidoNotFound(String id) {
        super("Pedido con id " + id + " no encontrado");
    }

}
