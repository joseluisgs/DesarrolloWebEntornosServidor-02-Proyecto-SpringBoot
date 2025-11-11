package dev.joseluisgs.tiendaapidaw;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching // Habilitamos el caché a nivel de aplicación
@EnableJpaAuditing // Habilitamos la auditoría, idual para el tiempo de creación y modificación
@EnableScheduling // ¡Es necesario para que funcione @Scheduled!
@Slf4j
public class TiendaApiDawApplication implements CommandLineRunner {
    @Value("${spring.profiles.active}")
    private String perfil;
    @Value("${server.port}")
    private String port;

    static void main(String[] args) {
        // Iniciamos la aplicación de Spring Boot
        SpringApplication.run(TiendaApiDawApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Aquí podemos ejecutar código al arrancar la aplicación
        // Este mensaje simplemente es para que lo veas en la consola,
        // no es necesario hacer este método si no lo vas a usar
        System.out.println("✅ Servidor escuchando en puerto: " + port + " y perfil: " + perfil + " 🚀");
    }


}
