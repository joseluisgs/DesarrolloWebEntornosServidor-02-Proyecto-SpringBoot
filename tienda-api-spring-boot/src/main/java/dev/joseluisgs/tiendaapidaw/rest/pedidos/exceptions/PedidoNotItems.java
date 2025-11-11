package dev.joseluisgs.tiendaapidaw.rest.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción de producto no encontrado
 * Status 404
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PedidoNotItems extends PedidoException {
    public PedidoNotItems(String id) {
        super("Pedido con id " + id + " no contiene items para ser procesado");
    }

}
