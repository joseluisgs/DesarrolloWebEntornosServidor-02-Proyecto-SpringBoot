package dev.joseluisgs.tiendaapidaw.config.websockets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class WebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable, WebSocketSender {
    private final String entity; // Entidad que se notifica

    // Sesiones de los clientes conectados, para recorrelos y enviarles mensajes (patrón observer)
    // es concurrente porque puede ser compartida por varios hilos
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public WebSocketHandler(String entity) {
        this.entity = entity;
    }

    /**
     * Cuando se establece la conexión con el servidor
     *
     * @param session Sesión del cliente
     * @throws Exception Error al establecer la conexión
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Conexión establecida con el servidor");
        log.info("Sesión: " + session);
        sessions.add(session);
        TextMessage message = new TextMessage("Updates Web socket: " + entity + " - Tienda API Spring Boot");
        log.info("Servidor envía: {}", message);
        session.sendMessage(message);
    }

    /**
     * Cuando se cierra la conexión con el servidor
     *
     * @param session Sesión del cliente
     * @param status  Estado de la conexión
     * @throws Exception Error al cerrar la conexión
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Conexión cerrada con el servidor: " + status);
        sessions.remove(session);
    }

    /**
     * Envía un mensaje a todos los clientes conectados
     *
     * @param message Mensaje a enviar
     * @throws IOException Error al enviar el mensaje
     */
    @Override
    public void sendMessage(String message) throws IOException {
        log.info("Enviar mensaje de cambios en la entidad: " + entity + " : " + message);
        // Enviamos el mensaje a todos los clientes conectados
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                log.info("Servidor WS envía: " + message);
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    /**
     * Envía mensajes periódicos a los clientes conectados para que sepan que el servidor sigue vivo
     *
     * @throws IOException Error al enviar el mensaje
     */
    @Scheduled(fixedRate = 1000) // Cada segundo
    @Override
    public void sendPeriodicMessages() throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                String broadcast = "server periodic message " + LocalTime.now();
                log.info("Server sends: " + broadcast);
                session.sendMessage(new TextMessage(broadcast));
            }
        }
    }

    /**
     * Maneja los mensajes de texto que le llegan al servidor, en este caso no hacemos nada porque no nos interesa
     * ya que el servidor no recibe mensajes de los clientes, solo les envía mensajes
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // No hago nada con los mensajes que me llegan
        // Si quisieramos un chat, por ejemplo, aquí lo gestionaríamos,
        // leeríamos el mensaje y lo enviaríamos a todos los clientes conectados
        /*
        String request = message.getPayload();
        log.info("Server received: " + request);
        String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));
        log.info("Server sends: " + response);
        session.sendMessage(new TextMessage(response));
        */
    }

    /**
     * Maneja los errores de transporte que le llegan al servidor
     *
     * @param session   Sesión del cliente
     * @param exception Excepción que se ha producido
     * @throws Exception Error al manejar el error
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Error de transporte con el servidor: " + exception.getMessage());
    }

    /**
     * Devuelve los subprotocolos que soporta el servidor
     *
     * @return Lista de subprotocolos
     */
    @Override
    public List<String> getSubProtocols() {
        return List.of("subprotocol.demo.websocket");
    }
}