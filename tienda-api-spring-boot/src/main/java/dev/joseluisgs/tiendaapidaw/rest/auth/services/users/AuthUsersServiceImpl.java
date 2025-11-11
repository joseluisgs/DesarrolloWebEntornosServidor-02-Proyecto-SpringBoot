package dev.joseluisgs.tiendaapidaw.rest.auth.services.users;

import dev.joseluisgs.tiendaapidaw.rest.auth.repositories.AuthUsersRepository;
import dev.joseluisgs.tiendaapidaw.rest.users.exceptions.UserNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
@Service("userDetailsService")
public class AuthUsersServiceImpl implements AuthUsersService {

    private final AuthUsersRepository authUsersRepository;

    @Autowired
    public AuthUsersServiceImpl(AuthUsersRepository authUsersRepository) {
        this.authUsersRepository = authUsersRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFound {
        return authUsersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("Usuario con username " + username + " no encontrado"));
    }
}