package dev.joseluisgs.tiendaapidaw.rest.productos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductoBadUuid extends ProductoException {
    public ProductoBadUuid(String uuid) {
        super("UUID: " + uuid + " no válido o de formato incorrecto");
    }
}
