package dev.joseluisgs.tiendaapidaw.rest.auth.services.authentication;

import dev.joseluisgs.tiendaapidaw.rest.auth.dto.JwtAuthResponse;
import dev.joseluisgs.tiendaapidaw.rest.auth.dto.UserSignInRequest;
import dev.joseluisgs.tiendaapidaw.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}