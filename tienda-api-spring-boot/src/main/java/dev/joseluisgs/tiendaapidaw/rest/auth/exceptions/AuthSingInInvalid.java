package dev.joseluisgs.tiendaapidaw.rest.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuthSingInInvalid extends AuthException {
    public AuthSingInInvalid(String message) {
        super(message);
    }
}