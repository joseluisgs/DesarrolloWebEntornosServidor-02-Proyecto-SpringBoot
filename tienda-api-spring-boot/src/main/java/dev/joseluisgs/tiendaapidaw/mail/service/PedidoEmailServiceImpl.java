package dev.joseluisgs.tiendaapidaw.mail.service;


import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class PedidoEmailServiceImpl implements PedidoEmailService {

    private final Logger logger = LoggerFactory.getLogger(PedidoEmailServiceImpl.class);
    private final EmailService emailService;

    public PedidoEmailServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Envía email de confirmación de pedido en HTML simple
     */
    @Override
    public void enviarConfirmacionPedido(Pedido pedido) {
        try {
            logger.info("Enviando confirmación HTML simple de pedido {} al cliente {}",
                    pedido.get_id(), pedido.getCliente().email());

            String subject = "Confirmación de tu pedido #" + pedido.get_id();
            String htmlBody = crearCuerpoEmailPedidoHtmlSimple(pedido);

            emailService.sendHtmlEmail(
                    pedido.getCliente().email(),
                    subject,
                    htmlBody
            );

            logger.info("Email HTML simple de confirmación enviado correctamente para el pedido {}", pedido.get_id());

        } catch (Exception e) {
            logger.error("Error enviando email de confirmación para el pedido {}: {}",
                    pedido.get_id(), e.getMessage());
            // No relanzamos la excepción para que no afecte al guardado del pedido
        }
    }

    /**
     * Envía email de confirmación de pedido en HTML completo y estilizado
     */
    @Override
    public void enviarConfirmacionPedidoHtml(Pedido pedido) {
        try {
            logger.info("Enviando confirmación HTML completa de pedido {} al cliente {}",
                    pedido.get_id(), pedido.getCliente().email());

            String subject = "✅ Confirmación de tu pedido #" + pedido.get_id();
            String htmlBody = crearCuerpoEmailPedidoHtmlCompleto(pedido);

            emailService.sendHtmlEmail(
                    pedido.getCliente().email(),
                    subject,
                    htmlBody
            );

            logger.info("Email HTML completo de confirmación enviado correctamente para el pedido {}", pedido.get_id());

        } catch (Exception e) {
            logger.error("Error enviando email HTML de confirmación para el pedido {}: {}",
                    pedido.get_id(), e.getMessage());
        }
    }

    /**
     * Crea el cuerpo del email en HTML simple (sin mucho CSS)
     */
    private String crearCuerpoEmailPedidoHtmlSimple(Pedido pedido) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));

        String lineasPedidoHtml = pedido.getLineasPedido().stream()
                .map(linea -> String.format("""
                                <li>
                                    <strong>Producto ID:</strong> %d | 
                                    <strong>Cantidad:</strong> %d | 
                                    <strong>Precio:</strong> %s | 
                                    <strong>Total:</strong> %s
                                </li>
                                """,
                        linea.getIdProducto(),
                        linea.getCantidad(),
                        currencyFormatter.format(linea.getPrecioProducto()),
                        currencyFormatter.format(linea.getTotal())))
                .collect(Collectors.joining(""));

        return String.format("""
                        <!DOCTYPE html>
                        <html lang="es">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Confirmación de Pedido</title>
                        </head>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px;">
                            <div style="max-width: 600px; margin: 0 auto;">
                        
                                <h1 style="color: #4CAF50; text-align: center;">¡Pedido Confirmado! 🎉</h1>
                        
                                <p><strong>¡Hola %s!</strong></p>
                                <p>Tu pedido ha sido confirmado y está siendo procesado.</p>
                        
                                <hr style="border: 1px solid #ddd; margin: 20px 0;">
                        
                                <h2 style="color: #4CAF50;">📝 Información del Pedido</h2>
                                <ul>
                                    <li><strong>Número:</strong> #%s</li>
                                    <li><strong>Fecha:</strong> %s</li>
                                    <li><strong>Estado:</strong> <span style="color: #4CAF50;">Confirmado</span></li>
                                </ul>
                        
                                <h2 style="color: #4CAF50;">👤 Datos del Cliente</h2>
                                <ul>
                                    <li><strong>Nombre:</strong> %s</li>
                                    <li><strong>Email:</strong> %s</li>
                                    <li><strong>Teléfono:</strong> %s</li>
                                </ul>
                        
                                <h2 style="color: #4CAF50;">🚚 Dirección de Entrega</h2>
                                <p>
                                    %s, %s<br>
                                    %s %s<br>
                                    %s, %s
                                </p>
                        
                                <h2 style="color: #4CAF50;">🛒 Detalles del Pedido</h2>
                                <ul>
                                    %s
                                </ul>
                        
                                <div style="background-color: #4CAF50; color: white; padding: 15px; text-align: center; border-radius: 5px; margin: 20px 0;">
                                    <h3 style="margin: 0;">Total de artículos: %d | TOTAL: %s</h3>
                                </div>
                        
                                <p><strong>🕐 Tu pedido será procesado en las próximas 24-48 horas.</strong></p>
                                <p>📧 Te mantendremos informado sobre el estado de tu envío.</p>
                        
                                <hr style="border: 1px solid #ddd; margin: 20px 0;">
                        
                                <p style="text-align: center;">
                                    <strong>¡Gracias por confiar en nosotros!</strong><br>
                                    <em>El equipo de Tienda</em>
                                </p>
                        
                                <p style="text-align: center; font-size: 12px; color: #666;">
                                    Este es un email automático, por favor no respondas a este mensaje.
                                </p>
                        
                            </div>
                        </body>
                        </html>
                        """,
                pedido.getCliente().nombreCompleto(),
                pedido.get_id(),
                pedido.getCreatedAt().format(formatter),
                pedido.getCliente().nombreCompleto(),
                pedido.getCliente().email(),
                pedido.getCliente().telefono(),
                pedido.getCliente().direccion().calle(),
                pedido.getCliente().direccion().numero(),
                pedido.getCliente().direccion().codigoPostal(),
                pedido.getCliente().direccion().ciudad(),
                pedido.getCliente().direccion().provincia(),
                pedido.getCliente().direccion().pais(),
                lineasPedidoHtml,
                pedido.getTotalItems(),
                currencyFormatter.format(pedido.getTotal())
        );
    }

    /**
     * Crea el cuerpo del email en HTML completo y estilizado
     */
    private String crearCuerpoEmailPedidoHtmlCompleto(Pedido pedido) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));

        String lineasPedidoHtml = pedido.getLineasPedido().stream()
                .map(linea -> String.format("""
                                <div class="linea-pedido">
                                    <div class="producto-info">
                                        <span class="producto-id">Producto ID: %d</span>
                                        <div class="producto-detalles">
                                            <span>Cantidad: <strong>%d</strong></span>
                                            <span>Precio: <strong>%s</strong></span>
                                            <span class="total-linea">Total: <strong>%s</strong></span>
                                        </div>
                                    </div>
                                </div>
                                """,
                        linea.getIdProducto(),
                        linea.getCantidad(),
                        currencyFormatter.format(linea.getPrecioProducto()),
                        currencyFormatter.format(linea.getTotal())))
                .collect(Collectors.joining(""));

        return String.format("""
                        <!DOCTYPE html>
                        <html lang="es">
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>Confirmación de Pedido</title>
                            <style>
                                body { 
                                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                                    line-height: 1.6; 
                                    color: #333; 
                                    margin: 0; 
                                    padding: 0; 
                                    background-color: #f4f4f4; 
                                }
                                .container { 
                                    max-width: 700px; 
                                    margin: 20px auto; 
                                    background-color: white; 
                                    border-radius: 10px; 
                                    box-shadow: 0 0 20px rgba(0,0,0,0.1); 
                                    overflow: hidden; 
                                }
                                .header { 
                                    background: linear-gradient(135deg, #4CAF50, #45a049); 
                                    color: white; 
                                    padding: 30px 20px; 
                                    text-align: center; 
                                }
                                .header h1 { 
                                    margin: 0; 
                                    font-size: 28px; 
                                    font-weight: 300; 
                                }
                                .header .pedido-num { 
                                    font-size: 18px; 
                                    margin-top: 10px; 
                                    opacity: 0.9; 
                                }
                                .content { 
                                    padding: 30px; 
                                }
                                .greeting { 
                                    font-size: 18px; 
                                    margin-bottom: 20px; 
                                    color: #4CAF50; 
                                }
                                .section { 
                                    margin: 30px 0; 
                                    padding: 20px; 
                                    background-color: #fafafa; 
                                    border-radius: 8px; 
                                    border-left: 4px solid #4CAF50; 
                                }
                                .section h3 { 
                                    color: #4CAF50; 
                                    margin-top: 0; 
                                    margin-bottom: 15px; 
                                    font-size: 20px; 
                                    display: flex; 
                                    align-items: center; 
                                }
                                .section h3::before { 
                                    margin-right: 10px; 
                                    font-size: 24px; 
                                }
                                .info-grid { 
                                    display: grid; 
                                    grid-template-columns: 1fr 1fr; 
                                    gap: 15px; 
                                    margin: 15px 0; 
                                }
                                .info-item { 
                                    padding: 10px; 
                                    background-color: white; 
                                    border-radius: 5px; 
                                    border: 1px solid #e0e0e0; 
                                }
                                .info-label { 
                                    font-weight: bold; 
                                    color: #666; 
                                    font-size: 14px; 
                                    display: block; 
                                    margin-bottom: 5px; 
                                }
                                .info-value { 
                                    color: #333; 
                                    font-size: 16px; 
                                }
                                .direccion { 
                                    background-color: white; 
                                    padding: 15px; 
                                    border-radius: 5px; 
                                    border: 1px solid #e0e0e0; 
                                    line-height: 1.8; 
                                }
                                .linea-pedido { 
                                    background-color: white; 
                                    padding: 15px; 
                                    margin: 10px 0; 
                                    border-radius: 5px; 
                                    border: 1px solid #e0e0e0; 
                                    border-left: 4px solid #4CAF50; 
                                }
                                .producto-info { 
                                    display: flex; 
                                    justify-content: space-between; 
                                    align-items: center; 
                                    flex-wrap: wrap; 
                                }
                                .producto-id { 
                                    font-weight: bold; 
                                    color: #4CAF50; 
                                    font-size: 16px; 
                                }
                                .producto-detalles { 
                                    display: flex; 
                                    gap: 15px; 
                                    flex-wrap: wrap; 
                                }
                                .producto-detalles span { 
                                    font-size: 14px; 
                                    color: #666; 
                                }
                                .total-linea { 
                                    color: #4CAF50 !important; 
                                    font-size: 16px !important; 
                                }
                                .total-section { 
                                    background: linear-gradient(135deg, #4CAF50, #45a049); 
                                    color: white; 
                                    padding: 25px; 
                                    text-align: center; 
                                    border-radius: 8px; 
                                    margin: 30px 0; 
                                }
                                .total-section h3 { 
                                    margin: 0; 
                                    font-size: 24px; 
                                    font-weight: 300; 
                                }
                                .status-info { 
                                    background-color: #e8f5e8; 
                                    padding: 20px; 
                                    border-radius: 8px; 
                                    border: 1px solid #4CAF50; 
                                    margin: 20px 0; 
                                }
                                .status-info p { 
                                    margin: 10px 0; 
                                    color: #2e7d32; 
                                }
                                .footer { 
                                    background-color: #f8f8f8; 
                                    padding: 25px; 
                                    text-align: center; 
                                    border-top: 1px solid #e0e0e0; 
                                }
                                .footer h4 { 
                                    color: #4CAF50; 
                                    margin: 0 0 10px 0; 
                                }
                                .footer p { 
                                    margin: 5px 0; 
                                    color: #666; 
                                }
                                .disclaimer { 
                                    font-size: 12px; 
                                    color: #999; 
                                    margin-top: 15px; 
                                }
                                @media (max-width: 600px) { 
                                    .info-grid { 
                                        grid-template-columns: 1fr; 
                                    }
                                    .producto-info { 
                                        flex-direction: column; 
                                        align-items: flex-start; 
                                    }
                                    .producto-detalles { 
                                        margin-top: 10px; 
                                    }
                                }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">
                                    <h1>✅ ¡Pedido Confirmado!</h1>
                                    <div class="pedido-num">Pedido #%s</div>
                                </div>
                        
                                <div class="content">
                                    <div class="greeting">
                                        ¡Hola <strong>%s</strong>! 👋
                                    </div>
                                    <p>Tu pedido ha sido confirmado exitosamente y ya está siendo procesado por nuestro equipo.</p>
                        
                                    <div class="section">
                                        <h3>📝 Información del Pedido</h3>
                                        <div class="info-grid">
                                            <div class="info-item">
                                                <span class="info-label">Número de Pedido</span>
                                                <span class="info-value">#%s</span>
                                            </div>
                                            <div class="info-item">
                                                <span class="info-label">Fecha</span>
                                                <span class="info-value">%s</span>
                                            </div>
                                            <div class="info-item">
                                                <span class="info-label">Estado</span>
                                                <span class="info-value" style="color: #4CAF50; font-weight: bold;">✅ Confirmado</span>
                                            </div>
                                            <div class="info-item">
                                                <span class="info-label">Artículos</span>
                                                <span class="info-value">%d productos</span>
                                            </div>
                                        </div>
                                    </div>
                        
                                    <div class="section">
                                        <h3>👤 Datos del Cliente</h3>
                                        <div class="info-grid">
                                            <div class="info-item">
                                                <span class="info-label">Nombre Completo</span>
                                                <span class="info-value">%s</span>
                                            </div>
                                            <div class="info-item">
                                                <span class="info-label">Email</span>
                                                <span class="info-value">%s</span>
                                            </div>
                                            <div class="info-item">
                                                <span class="info-label">Teléfono</span>
                                                <span class="info-value">%s</span>
                                            </div>
                                        </div>
                                    </div>
                        
                                    <div class="section">
                                        <h3>🚚 Dirección de Entrega</h3>
                                        <div class="direccion">
                                            <strong>%s, %s</strong><br>
                                            %s %s<br>
                                            %s, %s
                                        </div>
                                    </div>
                        
                                    <div class="section">
                                        <h3>🛒 Detalles del Pedido</h3>
                                        %s
                                    </div>
                        
                                    <div class="total-section">
                                        <h3>💰 Total del Pedido: %s</h3>
                                        <p>%d artículos en total</p>
                                    </div>
                        
                                    <div class="status-info">
                                        <p><strong>🕐 Tiempo de procesamiento:</strong> 24-48 horas</p>
                                        <p><strong>📧 Notificaciones:</strong> Te mantendremos informado sobre el estado de tu envío</p>
                                        <p><strong>📦 Seguimiento:</strong> Recibirás un código de seguimiento una vez que se envíe tu pedido</p>
                                    </div>
                                </div>
                        
                                <div class="footer">
                                    <h4>¡Gracias por confiar en nosotros!</h4>
                                    <p><strong>El equipo de Tienda</strong></p>
                                    <p>📞 Soporte: soporte@tienda.com | 📱 WhatsApp: +34 123 456 789</p>
                                    <div class="disclaimer">
                                        Este es un email automático, por favor no respondas a este mensaje.
                                    </div>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                pedido.get_id(),
                pedido.getCliente().nombreCompleto(),
                pedido.get_id(),
                pedido.getCreatedAt().format(formatter),
                pedido.getTotalItems(),
                pedido.getCliente().nombreCompleto(),
                pedido.getCliente().email(),
                pedido.getCliente().telefono(),
                pedido.getCliente().direccion().calle(),
                pedido.getCliente().direccion().numero(),
                pedido.getCliente().direccion().codigoPostal(),
                pedido.getCliente().direccion().ciudad(),
                pedido.getCliente().direccion().provincia(),
                pedido.getCliente().direccion().pais(),
                lineasPedidoHtml,
                currencyFormatter.format(pedido.getTotal()),
                pedido.getTotalItems()
        );
    }
}