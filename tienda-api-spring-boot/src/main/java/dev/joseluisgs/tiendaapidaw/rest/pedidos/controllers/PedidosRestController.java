package dev.joseluisgs.tiendaapidaw.rest.pedidos.controllers;

import dev.joseluisgs.tiendaapidaw.rest.pedidos.exceptions.*;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Pedido;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.services.PedidosService;
import dev.joseluisgs.tiendaapidaw.utils.pagination.PageResponse;
import dev.joseluisgs.tiendaapidaw.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("${api.version}/pedidos") // Es la ruta del controlador
@Slf4j
@PreAuthorize("hasRole('ADMIN')") // Solo los usuarios pueden acceder por defecto
public class PedidosRestController {
    private final PedidosService pedidosService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public PedidosRestController(PedidosService pedidosService, PaginationLinksUtils paginationLinksUtils) {
        this.pedidosService = pedidosService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    /**
     * Obtiene todos los pedidos
     *
     * @param page      Número de página
     * @param size      Tamaño de la página
     * @param sortBy    Campo de ordenación
     * @param direction Dirección de ordenación
     * @param request   Petición
     * @return Lista de pedidos paginada
     */
    @GetMapping()
    public ResponseEntity<PageResponse<Pedido>> getAllPedidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Obteniendo todos los pedidos");
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Pedido> pageResult = pedidosService.findAll(PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene un pedido por su id
     *
     * @param idPedido id del pedido
     * @return Pedido
     * @throws PedidoNotFound si no existe el pedido (404)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedido(@PathVariable("id") ObjectId idPedido) {
        log.info("Obteniendo pedido con id: " + idPedido);
        return ResponseEntity.ok(pedidosService.findById(idPedido));
    }

    /**
     * Obtiene los pedidos de un usuario
     *
     * @param idUsuario id del usuario
     * @param page      Número de página
     * @param size      Tamaño de la página
     * @param sortBy    Campo de ordenación
     * @param direction Dirección de ordenación
     * @return Lista de pedidos paginada
     * @throws PedidoNotFound si no existe el pedido (404)
     */
    @GetMapping("/usuario/{id}")
    public ResponseEntity<PageResponse<Pedido>> getPedidosByUsuario(
            @PathVariable("id") Long idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Obteniendo pedidos del usuario con id: " + idUsuario);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(pedidosService.findByIdUsuario(idUsuario, pageable), sortBy, direction));
    }

    /**
     * Crea un pedido para el usuario actual
     *
     * @param pedido pedido a crear
     * @return Pedido creado
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
     * @throws PedidoNotItems                      si no hay items en el pedido (400)
     * @throws ProductoBadPrice                    si el precio del producto no es correcto (400)
     * @throws ProductoNotFound                    si no existe el producto (404)
     * @throws ProductoNotStock                    si no hay stock del producto (400)
     */
    @PostMapping()
    public ResponseEntity<Pedido> createPedido(@Valid @RequestBody Pedido pedido) {
        log.info("Creando pedido: " + pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidosService.save(pedido));
    }

    /**
     * Actualiza un pedido
     *
     * @param idPedido id del pedido
     * @param pedido   pedido a actualizar
     * @return Pedido actualizado
     * @throws PedidoNotFound                      si no existe el pedido (404)
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
     * @throws PedidoNotItems                      si no hay items en el pedido (400)
     * @throws ProductoBadPrice                    si el precio del producto no es correcto (400)
     * @throws ProductoNotFound                    si no existe el producto (404)
     * @throws ProductoNotStock                    si no hay stock del producto (400)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable("id") ObjectId idPedido, @Valid @RequestBody Pedido pedido) {
        log.info("Actualizando pedido con id: " + idPedido);
        return ResponseEntity.ok(pedidosService.update(idPedido, pedido));
    }

    /**
     * Borra un pedido
     *
     * @param idPedido id del pedido
     * @return Pedido borrado
     * @throws PedidoNotFound si no existe el pedido (404)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Pedido> deletePedido(@PathVariable("id") ObjectId idPedido) {
        log.info("Borrando pedido con id: " + idPedido);
        pedidosService.delete(idPedido);
        return ResponseEntity.noContent().build();
    }
}
