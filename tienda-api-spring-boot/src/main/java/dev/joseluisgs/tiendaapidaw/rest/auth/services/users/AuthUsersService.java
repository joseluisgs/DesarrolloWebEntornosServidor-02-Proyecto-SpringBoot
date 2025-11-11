package dev.joseluisgs.tiendaapidaw.rest.auth.services.users;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

// Indicamos que es uns ervicio de detalles de usuario
// Es muy importante esta línea para decir que vamos a usar el servicio de usuarios Spring
// Otra forma de hacerlo es

/**
 * public interface AuthUsersService {
 * UserDetailsService userDetailsService();
 * }
 * <p>
 * y lugeo usarlo aqui con implements AuthUsersService
 */
public interface AuthUsersService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username);
}