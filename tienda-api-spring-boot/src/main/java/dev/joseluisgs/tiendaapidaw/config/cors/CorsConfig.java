package dev.joseluisgs.tiendaapidaw.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    /**
     * CORS: Configuración para desarrollo.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                // ¡OJO! allowedOrigins("*") permite CUALQUIER origen.
                // Esto está BIEN para desarrollo local, pero es INSEGURO para producción.

                registry.addMapping("/rest/producto/**")
                        .allowedOrigins("*") // Permitir todos para testing, si no cambiar por dominios específicos
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .maxAge(3600);

                registry.addMapping("/graphql/**")
                        .allowedOrigins("*") // <- EL CAMBIO IMPORTANTE, permitir todos los orígenes, si no cambiar por dominios específicos
                        .allowedMethods("POST", "OPTIONS") // Añadido OPTIONS para peticiones "preflight"
                        .maxAge(3600);

                registry.addMapping("/wss/**")
                        .allowedOrigins("*") // Permitir todos para testing, si no cambiar por dominios específicos
                        .allowedMethods("GET", "POST")
                        .maxAge(3600);
            }

        };
    }
}