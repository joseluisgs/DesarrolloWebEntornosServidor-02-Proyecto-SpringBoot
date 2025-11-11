package dev.joseluisgs.tiendaapidaw.rest.auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.joseluisgs.tiendaapidaw.rest.auth.dto.JwtAuthResponse;
import dev.joseluisgs.tiendaapidaw.rest.auth.dto.UserSignInRequest;
import dev.joseluisgs.tiendaapidaw.rest.auth.dto.UserSignUpRequest;
import dev.joseluisgs.tiendaapidaw.rest.auth.exceptions.AuthSingInInvalid;
import dev.joseluisgs.tiendaapidaw.rest.auth.exceptions.UserAuthNameOrEmailExisten;
import dev.joseluisgs.tiendaapidaw.rest.auth.exceptions.UserDiferentePasswords;
import dev.joseluisgs.tiendaapidaw.rest.auth.services.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class) // Extensión de Mockito para usarlo
class AuthenticationRestControllerTest {
    private final String myEndpoint = "/v1/auth";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc; // Cliente MVC
    @MockitoBean
    private AuthenticationService authenticationService;


    @Autowired
    public AuthenticationRestControllerTest(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        mapper.registerModule(new JavaTimeModule()); // Necesario para que funcione LocalDateTime
    }


    @Test
    void signUp() throws Exception {
        var userSignUpRequest = new UserSignUpRequest("Test", "Test", "test", "test@test.com", "test12345", "test12345");
        var jwtAuthResponse = new JwtAuthResponse("token");
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/signup";
        // Arrange
        when(authenticationService.signUp(any(UserSignUpRequest.class))).thenReturn(jwtAuthResponse);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);

        // Assert
        assertAll("signup",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        // Verify
        verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUp_WhenPasswordsDoNotMatch_ShouldThrowException() {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/signup";
        // Datos de prueba
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordComprobacion("password2");
        request.setEmail("test@test.com");
        request.setNombre("Test");
        request.setApellidos("User");

        // Mock del servicio
        when(authenticationService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserDiferentePasswords("Las contraseñas no coinciden"));

        // Llamada al método a probar y verificación de excepción
        assertThrows(UserDiferentePasswords.class, () -> authenticationService.signUp(request));

        // Verify
        verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUp_WhenUsernameOrEmailAlreadyExist_ShouldThrowException() {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/signup";
        // Datos de prueba
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("test@test.com");
        request.setNombre("Test");
        request.setApellidos("User");

        // Mock del servicio
        when(authenticationService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserAuthNameOrEmailExisten("El usuario con username " + request.getUsername() + " o email " + request.getEmail() + " ya existe"));

        // Llamada al método a probar y verificación de excepción
        assertThrows(UserAuthNameOrEmailExisten.class, () -> authenticationService.signUp(request));

        // Verify
        verify(authenticationService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    // Comprobar todas las validaciones
    @Test
    void signUp_BadRequest_When_Nombre_Apellidos_Email_Username_Empty_ShouldThrowException() throws Exception {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/signup";
        // Datos de prueba
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("");
        request.setNombre("");
        request.setApellidos("");

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        // Assert
        assertAll("signup",
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Nombre no puede estar")),
                () -> assertTrue(response.getContentAsString().contains("Apellidos no puede ")),
                () -> assertTrue(response.getContentAsString().contains("Username no puede"))
                //() -> assertTrue(response.getContentAsString().contains("Email no puede estar"))
        );
    }

    @Test
    void signIn() throws Exception {
        var userSignUpRequest = new UserSignUpRequest("Test", "Test", "test", "test@test.com", "test12345", "test12345");
        var jwtAuthResponse = new JwtAuthResponse("token");
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/signin";
        // Arrange
        when(authenticationService.signIn(any(UserSignInRequest.class))).thenReturn(jwtAuthResponse);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);

        // Assert
        assertAll("signin",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        // Verify
        verify(authenticationService, times(1)).signIn(any(UserSignInRequest.class));
    }

    // Faltan los test de bad request si los datos no son correctos

    @Test
    void signIn_Invalid() {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/signin";
        // Datos de prueba
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("testuser");
        request.setPassword("<PASSWORD>");

        // Mock del servicio
        when(authenticationService.signIn(any(UserSignInRequest.class))).thenThrow(new AuthSingInInvalid("Usuario o contraseña incorrectos"));

        // Llamada al método a probar y verificación de excepción
        assertThrows(AuthSingInInvalid.class, () -> authenticationService.signIn(request));

        // Verify
        verify(authenticationService, times(1)).signIn(any(UserSignInRequest.class));
    }

    // Comprobar todas las validaciones no hacerlo uno del tiron
    @Test
    void signIn_BadRequest_When_Username_Password_Empty_ShouldThrowException() throws Exception {
        // Localpoint
        var myLocalEndpoint = myEndpoint + "/signin";
        // Datos de prueba
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("");
        request.setPassword("");

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        // Assert
        assertAll("signin",
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("Username no puede"))
                //() -> assertTrue(response.getContentAsString().contains("Password no puede"))
        );
    }

}