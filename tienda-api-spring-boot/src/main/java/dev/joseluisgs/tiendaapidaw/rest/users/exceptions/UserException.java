package dev.joseluisgs.tiendaapidaw.rest.users.exceptions;

public abstract class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}
