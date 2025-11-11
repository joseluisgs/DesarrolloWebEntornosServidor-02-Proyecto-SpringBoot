package dev.joseluisgs.tiendaapidaw.mail.service;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;
    @Value("${app.mail.from:noreply@tienda.dev}")
    private String fromEmail;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender,
                            @Value("${app.mail.from:noreply@tienda.dev}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    /**
     * Envía un email simple (texto plano)
     */
    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            logger.info("Enviando email simple a: {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(fromEmail);

            mailSender.send(message);
            logger.info("Email simple enviado correctamente a: {}", to);

        } catch (Exception e) {
            logger.error("Error enviando email simple a {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Error enviando email: " + e.getMessage(), e);
        }
    }

    /**
     * Envía un email con HTML
     */
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            logger.info("Enviando email HTML a: {}", to);

            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = es HTML
            helper.setFrom(fromEmail);

            mailSender.send(message);
            logger.info("Email HTML enviado correctamente a: {}", to);

        } catch (MessagingException e) {
            logger.error("Error enviando email HTML a {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Error enviando email HTML: " + e.getMessage(), e);
        }
    }
}