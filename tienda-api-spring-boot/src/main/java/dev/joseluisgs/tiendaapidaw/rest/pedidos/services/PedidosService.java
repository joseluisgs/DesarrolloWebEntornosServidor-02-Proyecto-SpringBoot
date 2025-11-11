package dev.joseluisgs.tiendaapidaw.rest.pedidos.services;

import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Pedido;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PedidosService {
    Page<Pedido> findAll(Pageable pageable);

    Pedido findById(ObjectId idPedido);

    Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable);

    Pedido save(Pedido pedido);

    void delete(ObjectId idPedido);

    Pedido update(ObjectId idPedido, Pedido pedido);
}

