package dev.joseluisgs.tiendaapidaw.rest.users.controllers;

import dev.joseluisgs.tiendaapidaw.rest.pedidos.exceptions.*;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Pedido;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.services.PedidosService;
import dev.joseluisgs.tiendaapidaw.rest.users.dto.UserInfoResponse;
import dev.joseluisgs.tiendaapidaw.rest.users.dto.UserRequest;
import dev.joseluisgs.tiendaapidaw.rest.users.dto.UserResponse;
import dev.joseluisgs.tiendaapidaw.rest.users.exceptions.UserNameOrEmailExists;
import dev.joseluisgs.tiendaapidaw.rest.users.exceptions.UserNotFound;
import dev.joseluisgs.tiendaapidaw.rest.users.models.User;
import dev.joseluisgs.tiendaapidaw.rest.users.services.UsersService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("${api.version}/users") // Es la ruta del controlador
@PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
public class UsersRestController {
    private final UsersService usersService;
    private final PedidosService pedidosService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public UsersRestController(UsersService usersService, PedidosService pedidosService, PaginationLinksUtils paginationLinksUtils) {
        this.usersService = usersService;
        this.pedidosService = pedidosService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    /**
     * Obtiene todos los usuarios
     *
     * @param username  username del usuario
     * @param email     email del usuario
     * @param isDeleted si está borrado o no
     * @param page      página
     * @param size      tamaño
     * @param sortBy    campo de ordenación
     * @param direction dirección de ordenación
     * @param request   petición
     * @return Respuesta con la página de usuarios
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(required = false) Optional<String> username,
            @RequestParam(required = false) Optional<String> email,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("findAll: username: {}, email: {}, isDeleted: {}, page: {}, size: {}, sortBy: {}, direction: {}",
                username, email, isDeleted, page, size, sortBy, direction);
        // Creamos el objeto de ordenación
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<UserResponse> pageResult = usersService.findAll(username, email, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene un usuario por su id
     *
     * @param id del usuario, se pasa como parámetro de la URL /{id}
     * @return Usuario si existe
     * @throws UserNotFound si no existe el usuario (404)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
    public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
        log.info("findById: id: {}", id);
        return ResponseEntity.ok(usersService.findById(id));
    }

    /**
     * Crea un nuevo usuario
     *
     * @param userRequest usuario a crear
     * @return Usuario creado
     * @throws UserNameOrEmailExists               si el nombre de usuario o el email ya existen
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("save: userRequest: {}", userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.save(userRequest));
    }

    /**
     * Actualiza un usuario
     *
     * @param id          id del usuario
     * @param userRequest usuario a actualizar
     * @return Usuario actualizado
     * @throws UserNotFound                        si no existe el usuario (404)
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
     * @throws UserNameOrEmailExists               si el nombre de usuario o el email ya existen (400)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        log.info("update: id: {}, userRequest: {}", id, userRequest);
        return ResponseEntity.ok(usersService.update(id, userRequest));
    }

    /**
     * Borra un usuario
     *
     * @param id id del usuario
     * @return Respuesta vacía
     * @throws UserNotFound si no existe el usuario (404)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("delete: id: {}", id);
        usersService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene el usuario actual
     *
     * @param user usuario autenticado
     * @return Datos del usuario
     */
    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('ADMIN')") // Solo los admin pueden acceder
    public ResponseEntity<UserInfoResponse> me(@AuthenticationPrincipal User user) {
        log.info("Obteniendo usuario");
        // Esta autenticado, por lo que devolvemos sus datos ya sabemos su id
        return ResponseEntity.ok(usersService.findById(user.getId()));
    }

    /**
     * Actualiza el usuario actual
     *
     * @param user        usuario autenticado
     * @param userRequest usuario a actualizar
     * @return Usuario actualizado
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
     */
    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal User user, @Valid @RequestBody UserRequest userRequest) {
        log.info("updateMe: user: {}, userRequest: {}", user, userRequest);
        return ResponseEntity.ok(usersService.update(user.getId(), userRequest));
    }

    /**
     * Borra el usuario actual
     *
     * @param user usuario autenticado
     * @return Respuesta vacía
     */
    @DeleteMapping("/me/profile")
    @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
        log.info("deleteMe: user: {}", user);
        usersService.deleteById(user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene los pedidos del usuario actual
     *
     * @param user      usuario autenticado
     * @param page      página
     * @param size      tamaño
     * @param sortBy    campo de ordenación
     * @param direction dirección de ordenación
     * @return Respuesta con la página de pedidos
     */
    @GetMapping("/me/pedidos")
    @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
    public ResponseEntity<PageResponse<Pedido>> getPedidosByUsuario(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Obteniendo pedidos del usuario con id: " + user.getId());
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(pedidosService.findByIdUsuario(user.getId(), pageable), sortBy, direction));
    }

    /**
     * Obtiene un pedido del usuario actual
     *
     * @param user     usuario autenticado
     * @param idPedido id del pedido
     * @return Pedido
     * @throws UserNotFound si no existe el pedido
     */
    @GetMapping("/me/pedidos/{id}")
    @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
    public ResponseEntity<Pedido> getPedido(
            @AuthenticationPrincipal User user,
            @PathVariable("id") ObjectId idPedido
    ) {
        log.info("Obteniendo pedido con id: " + idPedido);
        return ResponseEntity.ok(pedidosService.findById(idPedido));
    }

    /**
     * Crea un pedido para el usuario actual
     *
     * @param user   usuario autenticado
     * @param pedido pedido a crear
     * @return Pedido creado
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
     * @throws PedidoNotItems                      si no hay items en el pedido (400)
     * @throws ProductoBadPrice                    si el precio del producto no es correcto (400)
     * @throws ProductoNotFound                    si no existe el producto (404)
     * @throws ProductoNotStock                    si no hay stock del producto (400)
     */
    @PostMapping("/me/pedidos")
    @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
    public ResponseEntity<Pedido> savePedido(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody Pedido pedido
    ) {
        log.info("Creando pedido: " + pedido);
        pedido.setIdUsuario(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidosService.save(pedido));
    }

    /**
     * Actualiza un pedido del usuario actual
     *
     * @param user     usuario autenticado
     * @param idPedido id del pedido
     * @param pedido   pedido a actualizar
     * @return Pedido actualizado
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
     * @throws PedidoNotFound                      si no existe el pedido (404)
     * @throws PedidoNotItems                      si no hay items en el pedido (400)
     * @throws ProductoBadPrice                    si el precio del producto no es correcto (400)
     * @throws ProductoNotFound                    si no existe el producto (404)
     * @throws ProductoNotStock                    si no hay stock del producto (400)
     */
    @PutMapping("/me/pedidos/{id}")
    @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
    public ResponseEntity<Pedido> updatePedido(
            @AuthenticationPrincipal User user,
            @PathVariable("id") ObjectId idPedido,
            @Valid @RequestBody Pedido pedido) {
        log.info("Actualizando pedido con id: " + idPedido);
        pedido.setIdUsuario(user.getId());
        return ResponseEntity.ok(pedidosService.update(idPedido, pedido));
    }

    /**
     * Borra un pedido del usuario actual
     *
     * @param user     usuario autenticado
     * @param idPedido id del pedido
     * @return Pedido borrado
     * @throws PedidoNotFound                      si no existe el pedido (404)
     * @throws HttpClientErrorException.BadRequest si hay algún error de validación (400)
     */
    @DeleteMapping("/me/pedidos/{id}")
    @PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
    public ResponseEntity<Void> deletePedido(
            @AuthenticationPrincipal User user,
            @PathVariable("id") ObjectId idPedido
    ) {
        log.info("Borrando pedido con id: " + idPedido);
        pedidosService.delete(idPedido);
        return ResponseEntity.noContent().build();
    }


    /**
     * Manejador de excepciones de Validación: 400 Bad Request
     *
     * @param ex excepción
     * @return Mapa de errores de validación con el campo y el mensaje
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
