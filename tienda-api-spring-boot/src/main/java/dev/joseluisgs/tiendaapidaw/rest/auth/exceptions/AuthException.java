package dev.joseluisgs.tiendaapidaw.rest.auth.exceptions;

public abstract class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
