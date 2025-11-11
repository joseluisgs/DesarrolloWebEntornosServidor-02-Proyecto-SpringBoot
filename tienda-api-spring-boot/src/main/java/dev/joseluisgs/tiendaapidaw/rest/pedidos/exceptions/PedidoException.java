package dev.joseluisgs.tiendaapidaw.rest.pedidos.exceptions;

public abstract class PedidoException extends RuntimeException {
    public PedidoException(String message) {
        super(message);
    }
}
