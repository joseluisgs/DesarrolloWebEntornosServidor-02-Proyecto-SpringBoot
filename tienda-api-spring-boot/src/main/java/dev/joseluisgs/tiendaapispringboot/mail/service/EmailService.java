package dev.joseluisgs.tiendaapispringboot.mail.service;


public interface EmailService {
    /**
     * Envía un email simple (texto plano)
     */
    void sendSimpleEmail(String to, String subject, String body);

    /**
     * Envía un email con HTML
     */
    void sendHtmlEmail(String to, String subject, String htmlBody);
}