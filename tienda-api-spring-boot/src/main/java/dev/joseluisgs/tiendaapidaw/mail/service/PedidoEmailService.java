package dev.joseluisgs.tiendaapidaw.mail.service;

import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Pedido;

public interface PedidoEmailService {

    /**
     * Envía email de confirmación de pedido en texto plano
     *
     * @param pedido El pedido para el cual enviar la confirmación
     */
    void enviarConfirmacionPedido(Pedido pedido);

    /**
     * Envía email de confirmación de pedido en formato HTML
     *
     * @param pedido El pedido para el cual enviar la confirmación
     */
    void enviarConfirmacionPedidoHtml(Pedido pedido);
}