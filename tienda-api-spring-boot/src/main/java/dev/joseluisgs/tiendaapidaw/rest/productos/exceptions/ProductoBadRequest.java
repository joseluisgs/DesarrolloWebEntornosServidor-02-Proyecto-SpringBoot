package dev.joseluisgs.tiendaapidaw.rest.productos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductoBadRequest extends ProductoException {
    public ProductoBadRequest(String message) {
        super(message);
    }
}
