package dev.joseluisgs.tiendaapispringboot.rest.productos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.joseluisgs.tiendaapispringboot.config.websockets.WebSocketConfig;
import dev.joseluisgs.tiendaapispringboot.config.websockets.WebSocketHandler;
import dev.joseluisgs.tiendaapispringboot.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapispringboot.rest.categorias.repositories.CategoriasRepository;
import dev.joseluisgs.tiendaapispringboot.rest.productos.dto.ProductoCreateRequest;
import dev.joseluisgs.tiendaapispringboot.rest.productos.dto.ProductoResponse;
import dev.joseluisgs.tiendaapispringboot.rest.productos.dto.ProductoUpdateRequest;
import dev.joseluisgs.tiendaapispringboot.rest.productos.exceptions.ProductoBadRequest;
import dev.joseluisgs.tiendaapispringboot.rest.productos.exceptions.ProductoBadUuid;
import dev.joseluisgs.tiendaapispringboot.rest.productos.exceptions.ProductoNotFound;
import dev.joseluisgs.tiendaapispringboot.rest.productos.mappers.ProductoMapper;
import dev.joseluisgs.tiendaapispringboot.rest.productos.models.Producto;
import dev.joseluisgs.tiendaapispringboot.rest.productos.repositories.ProductosRepository;
import dev.joseluisgs.tiendaapispringboot.rest.storage.services.StorageService;
import dev.joseluisgs.tiendaapispringboot.websockets.notifications.dto.ProductoNotificationResponse;
import dev.joseluisgs.tiendaapispringboot.websockets.notifications.mapper.ProductoNotificationMapper;
import dev.joseluisgs.tiendaapispringboot.websockets.notifications.models.Notificacion;
import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación de nuestro servicio de productos
 * Aquí implementamos la lógica de negocio
 * Además es cacheable
 */
@Service
@CacheConfig(cacheNames = {"productos"})
@Slf4j
public class ProductosServiceImpl implements ProductosService {
    private final ProductosRepository productosRepository;
    private final CategoriasRepository categoriasRepository;
    private final ProductoMapper productosMapper;
    private final StorageService storageService;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    private final ProductoNotificationMapper productoNotificationMapper;
    private WebSocketHandler webSocketService;

    @Autowired
    public ProductosServiceImpl(ProductosRepository productosRepository, CategoriasRepository categoriasRepository, ProductoMapper productoMapper, StorageService storageService, WebSocketConfig webSocketConfig, ProductoNotificationMapper productoNotificationMapper) {
        this.productosRepository = productosRepository;
        this.categoriasRepository = categoriasRepository;
        this.productosMapper = productoMapper;
        this.storageService = storageService;
        this.webSocketConfig = webSocketConfig;
        // Para enviar mensajes a los clientes ws normales
        webSocketService = webSocketConfig.webSocketProductosHandler();
        mapper = new ObjectMapper();
        this.productoNotificationMapper = productoNotificationMapper;
        // mapper.registerModule(new JavaTimeModule()); // Necesario para que funcione LocalDateTime
    }

    /**
     * Busca todos los productos
     *
     * @param marca     Marca del producto
     * @param categoria Categoría del producto
     * @param modelo    Modelo del producto
     * @param isDeleted Si está borrado o no
     * @param precioMax Precio máximo
     * @param stockMin  Stock mínimo
     * @param pageable  Paginación y ordenación
     * @return Lista de productos
     */
    @Override
    public Page<ProductoResponse> findAll(Optional<String> marca, Optional<String> categoria, Optional<String> modelo, Optional<Boolean> isDeleted, Optional<Double> precioMax, Optional<Double> stockMin, Pageable pageable) {
        // Criterio de búsqueda por marca
        Specification<Producto> specMarcaProducto = (root, query, criteriaBuilder) ->
                marca.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("marca")), "%" + m.toLowerCase() + "%")) // Buscamos por marca
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay marca, no filtramos

        // Criterio de búsqueda por categoría
        Specification<Producto> specCategoriaProducto = (root, query, criteriaBuilder) ->
                categoria.map(c -> {
                    Join<Producto, Categoria> categoriaJoin = root.join("categoria"); // Join con categoría
                    return criteriaBuilder.like(criteriaBuilder.lower(categoriaJoin.get("nombre")), "%" + c.toLowerCase() + "%"); // Buscamos por nombre
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay categoría, no filtramos

        // Criterio de búsqueda por isDeleted
        Specification<Producto> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por modelo
        Specification<Producto> specModeloProducto = (root, query, criteriaBuilder) ->
                modelo.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("modelo")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por precioMax, es decir tiene que ser menor o igual
        Specification<Producto> specPrecioMaxProducto = (root, query, criteriaBuilder) ->
                precioMax.map(p -> criteriaBuilder.lessThanOrEqualTo(root.get("precio"), p))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por stockMin, es decir tiene que ser menor o igual
        Specification<Producto> specStockMinProducto = (root, query, criteriaBuilder) ->
                stockMin.map(s -> criteriaBuilder.lessThanOrEqualTo(root.get("stock"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Combinamos las especificaciones
        Specification<Producto> criterio = Specification.where(specMarcaProducto)
                .and(specCategoriaProducto)
                .and(specIsDeleted)
                .and(specModeloProducto)
                .and(specPrecioMaxProducto)
                .and(specStockMinProducto);
        return productosRepository.findAll(criterio, pageable).map(productosMapper::toProductResponse);
    }

    /**
     * Busca un producto por su id
     *
     * @param id Id del producto
     * @return Producto encontrado
     * @throws ProductoNotFound Si no lo encuentra
     */
    @Override
    @Cacheable(key = "#id")
    public ProductoResponse findById(Long id) {
        log.info("Buscando producto por id: " + id);
        return productosMapper.toProductResponse(productosRepository.findById(id).orElseThrow(() -> new ProductoNotFound(id)));
    }

    /**
     * Busca un producto por su uuid
     *
     * @param uuid Uuid del producto en formato string
     * @return Producto encontrado
     * @throws ProductoNotFound Si no lo encuentra
     * @throws ProductoBadUuid  Si el uuid no es válido
     */
    @Override
    @Cacheable(key = "#uuid")
    public ProductoResponse findbyUuid(String uuid) {
        log.info("Buscando producto por uuid: " + uuid);
        try {
            var myUUID = UUID.fromString(uuid);
            return productosMapper.toProductResponse(productosRepository.findByUuid(myUUID).orElseThrow(() -> new ProductoNotFound(myUUID)));
        } catch (IllegalArgumentException e) {
            throw new ProductoBadUuid(uuid);
        }
    }

    /**
     * Comprueba si existe la categoría
     *
     * @param nombreCategoria Nombre de la categoría
     */
    private Categoria checkCategoria(String nombreCategoria) {
        log.info("Buscando categoría por nombre: " + nombreCategoria);
        // Buscamos la categoría por su nombre, debe existir y no estar borrada
        var categoria = categoriasRepository.findByNombreEqualsIgnoreCase(nombreCategoria);
        if (categoria.isEmpty() || categoria.get().getIsDeleted()) {
            throw new ProductoBadRequest("La categoría " + nombreCategoria + " no existe o está borrada");
        }
        return categoria.get();
    }

    /**
     * Guarda un producto
     *
     * @param productoCreateRequest Producto a guardar
     * @return Producto guardado
     */
    @Override
    @CachePut(key = "#result.id")
    public ProductoResponse save(ProductoCreateRequest productoCreateRequest) {
        log.info("Guardando producto: " + productoCreateRequest);
        // Comprobamos que la categoría
        var categoria = checkCategoria(productoCreateRequest.getCategoria());
        // Creamos el producto nuevo con los datos que nos vienen del dto, podríamos usar el mapper
        // Lo guardamos en el repositorio
        var productoSaved = productosRepository.save(productosMapper.toProduct(productoCreateRequest, categoria));
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.CREATE, productoSaved);
        // Devolvemos el producto guardado
        return productosMapper.toProductResponse(productoSaved);
    }

    /**
     * Actualiza un producto
     *
     * @param id                    Id del producto a actualizar
     * @param productoUpdateRequest Producto a actualizar
     * @return Producto actualizado
     * @throws ProductoNotFound Si no lo encuentra
     */
    @Override
    @CachePut(key = "#result.id")
    @Transactional
    public ProductoResponse update(Long id, ProductoUpdateRequest productoUpdateRequest) {
        log.info("Actualizando producto por id: " + id);
        // Si no existe lanza excepción, por eso ya llamamos a lo que hemos implementado antes
        var productoActual = productosRepository.findById(id).orElseThrow(() -> new ProductoNotFound(id));
        // Buscamos la categoría por su nombre
        // Si no tenemos categoría, no la actualizamos
        Categoria categoria = null;
        if (productoUpdateRequest.getCategoria() != null && !productoUpdateRequest.getCategoria().isEmpty()) {
            // Buscamos la categoría por su nombre, debe existir y no estar borrada
            categoria = checkCategoria(productoUpdateRequest.getCategoria());
        } else {
            categoria = productoActual.getCategoria();
        }
        // Actualizamos el producto con los datos que nos vienen del dto, podríamos usar el mapper
        // Lo guardamos en el repositorio
        var productoUpdated = productosRepository.save(productosMapper.toProduct(productoUpdateRequest, productoActual, categoria));
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.UPDATE, productoUpdated);
        // Devolvemos el producto actualizado
        return productosMapper.toProductResponse(productoUpdated);
    }

    /**
     * Borra un producto
     *
     * @param id Id del producto a borrar
     * @throws ProductoNotFound Si no lo encuentra
     */
    @Override
    @CachePut(key = "#id")
    @Transactional // Para que se haga todo o nada y no se quede a medias (por el update)
    public void deleteById(Long id) {
        log.debug("Borrando producto por id: " + id);
        // Si no existe lanza excepción, por eso ya llamamos a lo que hemos implementado antes
        var prod = productosRepository.findById(id).orElseThrow(() -> new ProductoNotFound(id));
        // Lo borramos del repositorio
        productosRepository.deleteById(id);
        // O lo marcamos como borrado, para evitar problemas de cascada, no podemos borrar productos en pedidos!!!
        //productosRepository.updateIsDeletedToTrueById(id);
        // Borramos la imagen del producto si existe y es distinta a la por defecto
        if (prod.getImagen() != null && !prod.getImagen().equals(Producto.IMAGE_DEFAULT)) {
            storageService.delete(prod.getImagen());
        }
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.DELETE, prod);
    }

    /**
     * Actualiza la imagen de un producto
     *
     * @param id    Id del producto a actualizar
     * @param image Imagen a actualizar del producto en formato Multipart
     * @return Producto actualizado
     * @throws ProductoNotFound Si no lo encuentra
     */
    @Override
    @CachePut(key = "#result.id")
    @Transactional
    public ProductoResponse updateImage(Long id, MultipartFile image, Boolean withUrl) {
        log.info("Actualizando imagen de producto por id: " + id);
        // Si no existe lanza excepción, por eso ya llamamos a lo que hemos implementado antes
        var productoActual = productosRepository.findById(id).orElseThrow(() -> new ProductoNotFound(id));
        // Borramos la imagen anterior si existe y no es la de por defecto
        if (productoActual.getImagen() != null && !productoActual.getImagen().equals(Producto.IMAGE_DEFAULT)) {
            storageService.delete(productoActual.getImagen());
        }
        String imageStored = storageService.store(image);
        // Si quiero la url completa
        String imageUrl = !withUrl ? imageStored : storageService.getUrl(imageStored);
        //storageService.getUrl(imageStored); // Si quiero la url completa
        // Clonamos el producto con la nueva imagen, porque inmutabilidad de los objetos
        var productoActualizado = Producto.builder()
                .id(productoActual.getId())
                .marca(productoActual.getMarca())
                .modelo(productoActual.getModelo())
                .descripcion(productoActual.getDescripcion())
                .precio(productoActual.getPrecio())
                .imagen(imageUrl)
                .stock(productoActual.getStock())
                .createdAt(productoActual.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .uuid(productoActual.getUuid())
                .isDeleted(productoActual.getIsDeleted())
                .categoria(productoActual.getCategoria())
                .build();

        // Lo guardamos en el repositorio
        var productoUpdated = productosRepository.save(productoActualizado);
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.UPDATE, productoUpdated);
        // Devolvemos el producto actualizado
        return productosMapper.toProductResponse(productoUpdated);
    }

    void onChange(Notificacion.Tipo tipo, Producto data) {
        log.debug("Servicio de productos onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketProductosHandler();
        }

        try {
            Notificacion<ProductoNotificationResponse> notificacion = new Notificacion<>(
                    "PRODUCTOS",
                    tipo,
                    productoNotificationMapper.toProductNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");
            // Enviamos el mensaje a los clientes ws con un hilo, si hay muchos clientes, puede tardar
            // no bloqueamos el hilo principal que atiende las peticiones http
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.setName("WebsocketProducto-" + data.getId());
            senderThread.setDaemon(true); // Para que no impida que la aplicación se cierre
            senderThread.start();
            log.info("Hilo de websocket iniciado: {}", data.getId());
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    // Para los test
    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }
}
