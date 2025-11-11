package dev.joseluisgs.tiendaapispringboot.novedades;

import dev.joseluisgs.tiendaapispringboot.mail.service.EmailService;
import dev.joseluisgs.tiendaapispringboot.rest.productos.models.Producto;
import dev.joseluisgs.tiendaapispringboot.rest.productos.services.ProductosService;
import dev.joseluisgs.tiendaapispringboot.rest.users.models.User;
import dev.joseluisgs.tiendaapispringboot.rest.users.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NovedadesTask {

    private final ProductosService productosService;
    private final EmailService emailService;
    private final UsersService usersService;

    // Guarda la última vez que se envió el email de novedades (inicialízala por defecto a -1 día)
    private LocalDateTime ultimaEjecucion = LocalDateTime.now().minusDays(1);

    @Autowired
    public NovedadesTask(ProductosService productosService,
                         EmailService emailService,
                         UsersService usersService) {
        this.productosService = productosService;
        this.emailService = emailService;
        this.usersService = usersService;
    }

    // Ejecutar cada día a las 8:30am
    @Scheduled(cron = "0 30 8 * * ?")
    public void enviarCorreoNovedades() {
        LocalDateTime ahora = LocalDateTime.now();

        // Obtiene los productos creados entre la última ejecución y ahora
        List<Producto> nuevosProductos = productosService.findByCreatedAtBetween(ultimaEjecucion, ahora);

        if (!nuevosProductos.isEmpty()) {
            StringBuilder html = new StringBuilder();
            html.append("<h1>¡Novedades en la tienda!</h1>");
            html.append("<ul>");
            for (Producto producto : nuevosProductos) {
                html.append("<li>")
                        .append("<strong>").append(producto.getMarca()).append("</strong>")
                        .append(" - ").append(producto.getModelo())
                        .append(" - ").append(producto.getPrecio()).append(" €")
                        .append(" - ").append(producto.getDescripcion())
                        .append("<img src='").append(producto.getImagen() == null ? Producto.IMAGE_DEFAULT : producto.getImagen())
                        .append("</li>");
            }
            html.append("</ul>");
            html.append("<p>Total de nuevos productos: <b>").append(nuevosProductos.size()).append("</b></p>");

            // Obtener todos los usuarios y enviarles el correo
            List<User> usuarios = usersService.findAllActiveUsers(); // Asegúrate de tener este método
            for (User user : usuarios) {
                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    Thread emailThread = getThread(user, html);

                    // Iniciar el hilo (no bloqueante)
                    emailThread.start();

                }
            }
        }
        // Actualiza la fecha de última ejecución
        ultimaEjecucion = ahora;
    }

    private Thread getThread(User user, StringBuilder html) {
        Thread emailThread = new Thread(() -> {
            try {

                // Enviar el email
                emailService.sendHtmlEmail(
                        user.getEmail(),
                        "Novedades de productos en la tienda",
                        html.toString()
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Configurar el hilo
        emailThread.setName("EmailSender-Novedades-" + user.getId());
        emailThread.setDaemon(true); // Para que no impida que la aplicación se cierre
        return emailThread;
    }
}