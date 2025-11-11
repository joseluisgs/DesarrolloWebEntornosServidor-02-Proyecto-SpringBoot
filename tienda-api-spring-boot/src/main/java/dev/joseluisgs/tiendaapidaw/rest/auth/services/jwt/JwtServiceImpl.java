package dev.joseluisgs.tiendaapidaw.rest.auth.services.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio de JWT
 */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String jwtSigningKey;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Extrae el nombre de usuario del token
     *
     * @param token token
     * @return nombre de usuario
     */
    @Override
    public String extractUserName(String token) {
        log.info("Extracting username from token " + token);
        return extractClaim(token, DecodedJWT::getSubject);
    }

    /**
     * Genera un token
     *
     * @param userDetails Detalles del usuario
     * @return token
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user " + userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Genera un token con datos extra
     *
     * @param token       token
     * @param userDetails Detalles del usuario
     * @return token
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Validating token " + token + " for user " + userDetails.getUsername());
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Genera un token con datos extra
     *
     * @param token           token
     * @param claimsResolvers Detalles del usuario
     * @return token
     */
    private <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolvers) {
        log.info("Extracting claim from token " + token);
        final DecodedJWT decodedJWT = JWT.decode(token);
        return claimsResolvers.apply(decodedJWT);
    }

    /**
     * Genera un token con datos extra
     *
     * @param extraClaims Datos extra
     * @param userDetails Detalles del usuario
     * @return token
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        // Preparamos el token
        Algorithm algorithm = Algorithm.HMAC512(getSigningKey());
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + (1000 * jwtExpiration));

        return JWT.create()
                .withHeader(createHeader())
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .withClaim("extraClaims", extraClaims)
                .sign(algorithm);
    }

    /**
     * Comprueba si el token ha expirado
     *
     * @param token token
     * @return true si ha expirado
     */
    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token
     *
     * @param token token
     * @return fecha de expiración
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAt);
    }

    /**
     * Crea el encabezado del token
     *
     * @return encabezado del token
     */
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        return header;
    }

    /**
     * Obtiene la clave de firma
     *
     * @return clave de firma
     */
    private byte[] getSigningKey() {
        return Base64.getEncoder().encode(jwtSigningKey.getBytes());

    }
}