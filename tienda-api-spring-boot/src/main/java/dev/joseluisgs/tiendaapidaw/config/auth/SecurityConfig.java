package dev.joseluisgs.tiendaapidaw.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity // Habilitamos la seguridad
@EnableMethodSecurity // Habilitamos la seguridad a nivel de método
public class SecurityConfig {
    // private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${api.version}")
    private String apiVersion;

    @Autowired
    public SecurityConfig(UserDetailsService userService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Podemos decir que forzamos el uso de HTTPS, para algunas rutas de la API o todas
                // Requerimos HTTPS para todas las peticiones, pero ojo que devuelve 302 para los test
                // .requiresChannel(channel -> channel.anyRequest().requiresSecure())

                // Deshabilitamos CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // Sesiones
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                // Lo primero es decir a qué URLs queremos dar acceso libre
                // Lista balanca de comprobación

                .authorizeHttpRequests(request -> request
                        .requestMatchers("/error/**").permitAll()
                        // Abrimos a Swagger -- Quitar en producción
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Permitimos el acceso a los recursos estáticos
                        .requestMatchers("/static/**").permitAll()
                        // Permitimos el acceso a las imagenes
                        .requestMatchers("/images/**").permitAll()
                        // Permitimos lso accesos a los webjars
                        .requestMatchers("/webjars/**").permitAll()
                        // Permitimos el acceso a la web de productos
                        .requestMatchers("/productos/**").permitAll()
                        // Websockets para notificaciones
                        .requestMatchers("/ws/**").permitAll()
                        // Storage
                        .requestMatchers("/storage/**").permitAll()
                        // Otras rutas de la API podemos permitiras o no....
                        .requestMatchers("/" + apiVersion + "/**").permitAll()
                        // graphql
                        .requestMatchers("/graphql", "/graphiql", "/graphiql/**").permitAll()
                        // Podríamos jugar con permismos por ejemplo para una ruta concreta
                        //.requestMatchers("/" + apiVersion + "/auth/me").hasRole("ADMIN")
                        // O con un acción HTTP, POST, PUT, DELETE, etc.
                        //.requestMatchers(GET, "/" + apiVersion + "/auth/me").hasRole("ADMIN")
                        // O con un patrón de ruta
                        //.regexMatchers("/" + apiVersion + "/auth/me").hasRole("ADMIN")
                        // El resto de peticiones tienen que estar autenticadas
                        .anyRequest().authenticated())

                // Añadimos el filtro de autenticación
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Devolvemos la configuración
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }


}