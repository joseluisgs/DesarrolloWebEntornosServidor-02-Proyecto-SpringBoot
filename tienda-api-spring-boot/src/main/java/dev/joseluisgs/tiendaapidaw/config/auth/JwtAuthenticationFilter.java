package dev.joseluisgs.tiendaapidaw.config.auth;

import dev.joseluisgs.tiendaapidaw.rest.auth.services.jwt.JwtService;
import dev.joseluisgs.tiendaapidaw.rest.auth.services.users.AuthUsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthUsersService authUsersService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, AuthUsersService authUsersService) {
        this.jwtService = jwtService;
        this.authUsersService = authUsersService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        log.info("Iniciando el filtro de autenticación");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        UserDetails userDetails = null;
        String userName = null;

        // Si no tenemos cabecera o no empieza por Bearer, no hacemos nada
        if (!StringUtils.hasText(authHeader) || !StringUtils.startsWithIgnoreCase(authHeader, "Bearer ")) {
            log.info("No se ha encontrado cabecera de autenticación, se ignora");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Se ha encontrado cabecera de autenticación, se procesa");
        // Si tenemos cabecera, la extraemos y comprobamos que sea válida
        jwt = authHeader.substring(7);
        // Lo primero que debemos ver es que el token es válido
        try {
            userName = jwtService.extractUserName(jwt);
        } catch (Exception e) {
            log.info("Token no válido");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no autorizado o no válido");
            return;
        }
        log.info("Usuario autenticado: {}", userName);
        if (StringUtils.hasText(userName)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Comprobamos que el usuario existe y que el token es válido
            log.info("Comprobando usuario y token");
            try {
                userDetails = authUsersService.loadUserByUsername(userName);
            } catch (Exception e) {
                log.info("Usuario no encontrado: {}", userName);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autorizado");
                return;
            }
            authUsersService.loadUserByUsername(userName);
            log.info("Usuario encontrado: {}", userDetails);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("JWT válido");
                // Si es válido, lo autenticamos en el contexto de seguridad
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                // Añadimos los detalles de la petición
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Lo añadimos al contexto de seguridad
                context.setAuthentication(authToken);
                // Y lo añadimos al contexto de seguridad
                SecurityContextHolder.setContext(context);
            }
        }
        // Y seguimos con la petición
        filterChain.doFilter(request, response);
    }
}