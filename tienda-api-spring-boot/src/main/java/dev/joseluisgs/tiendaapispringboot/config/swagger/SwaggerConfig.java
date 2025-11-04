package dev.joseluisgs.tiendaapispringboot.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {

    @Value("${api.version}")
    private String apiVersion;

    // Añadimos la configuración de JWT
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("API REST Tienda Spring Boot DAW 2025/2026")
                                .version("1.0.0")
                                .description("API de ejemplo del curso Desarrollo de un API REST con Spring Boot para 2º DAW. 2025/2026")
                                .termsOfService("https://joseluisgs.dev/docs/license/")
                                .license(
                                        new License()
                                                .name("CC BY-NC-SA 4.0")
                                                .url("https://joseluisgs.dev/docs/license/")
                                )
                                .contact(
                                        new Contact()
                                                .name("José Luis González Sánchez")
                                                .email("joseluis.gonzales@iesluisvives.org")
                                                .url("https://joseluisgs.dev")
                                )

                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Documentación del Proyecto")
                                .url("https://github.com/joseluisgs/DesarrolloWebEntornosServidor-02-2023-2024")
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("GitHub del Proyecto")
                                .url("https://github.com/joseluisgs/DesarrolloWebEntornosServidor-02-Proyecto-2023-2024")
                )
                // Añadimos la seguridad JWT
                .addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()));
    }


    @Bean
    GroupedOpenApi httpApi() {
        return GroupedOpenApi.builder()
                .group("https")
                // Algunas rutas son JWT
                // .pathsToMatch("/v1/**") // Todas las rutas
                .pathsToMatch("/" + apiVersion + "/productos/**") //Solo productos
                .displayName("API Tienda Spring Boot DAW 2025/2026")
                .build();
    }
}