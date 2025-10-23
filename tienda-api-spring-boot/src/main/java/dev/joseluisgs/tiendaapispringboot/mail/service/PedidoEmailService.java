package dev.joseluisgs.tiendaapispringboot.mail.service;

import dev.joseluisgs.tiendaapispringboot.rest.pedidos.models.Pedido;

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