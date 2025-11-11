package dev.joseluisgs.tiendaapidaw.rest.productos.services;

import dev.joseluisgs.tiendaapidaw.config.websockets.WebSocketConfig;
import dev.joseluisgs.tiendaapidaw.config.websockets.WebSocketHandler;
import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapidaw.rest.categorias.repositories.CategoriasRepository;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoCreateRequest;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoResponse;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoUpdateRequest;
import dev.joseluisgs.tiendaapidaw.rest.productos.exceptions.ProductoBadRequest;
import dev.joseluisgs.tiendaapidaw.rest.productos.exceptions.ProductoBadUuid;
import dev.joseluisgs.tiendaapidaw.rest.productos.exceptions.ProductoNotFound;
import dev.joseluisgs.tiendaapidaw.rest.productos.mappers.ProductoMapper;
import dev.joseluisgs.tiendaapidaw.rest.productos.models.Producto;
import dev.joseluisgs.tiendaapidaw.rest.productos.repositories.ProductosRepository;
import dev.joseluisgs.tiendaapidaw.rest.storage.services.StorageService;
import dev.joseluisgs.tiendaapidaw.websockets.notifications.mapper.ProductoNotificationMapper;
import dev.joseluisgs.tiendaapidaw.websockets.notifications.models.Notificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductosServiceImplTest {

    private final Categoria categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "DEPORTES", LocalDateTime.now(), LocalDateTime.now(), false);

    private final Producto producto1 = Producto.builder()
            .id(1L)
            .marca("Adidas")
            .modelo("Zapatillas")
            .descripcion("Zapatillas de deporte")
            .precio(100.0)
            .imagen("http://placeimg.com/640/480/people")
            .stock(5)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .categoria(categoria)
            .build();

    private final ProductoResponse productoResponse1 = ProductoResponse.builder()
            .id(1L)
            .marca("Adidas")
            .modelo("Zapatillas")
            .descripcion("Zapatillas de deporte")
            .precio(100.0)
            .imagen("http://placeimg.com/640/480/people")
            .stock(5)
            .categoria(categoria.getNombre())
            .build();

    private final Producto producto2 = Producto.builder()
            .id(2L)
            .marca("Nike")
            .modelo("Zapatillas")
            .descripcion("Zapatillas de deporte")
            .precio(100.0)
            .imagen("http://placeimg.com/640/480/people")
            .stock(5)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .isDeleted(false)
            .categoria(categoria)
            .build();
private final ProductoResponse productoResponse2 = ProductoResponse.builder()
            .id(2L)
            .marca("Nike")
            .modelo("Zapatillas")
            .descripcion("Zapatillas de deporte")
            .precio(100.0)
            .imagen("http://placeimg.com/640/480/people")
            .stock(5)
            .categoria(categoria.getNombre())
            .build();

    WebSocketHandler webSocketHandlerMock = mock(WebSocketHandler.class);
    @Mock
    private ProductosRepository productosRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private CategoriasRepository categoriasRepository;
    @Mock
    private ProductoMapper productoMapper;
    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private ProductoNotificationMapper productoNotificationMapper;
    @InjectMocks
    private ProductosServiceImpl productoService;
    @Captor // Captor de argumentos
    private ArgumentCaptor<Producto> productoCaptor;


    @BeforeEach
    void setUp() {
        productoService.setWebSocketService(webSocketHandlerMock);
    }

    @Test
    void findAll_ShouldReturnAllProducts_WhenNoParametersProvided() {
        // Arrange
        List<Producto> expectedProducts = Arrays.asList(producto1, producto2);
        List<ProductoResponse> expectedResponseProducts = Arrays.asList(productoResponse1, productoResponse2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending()); // ejemplo de creación de un objeto Pageable
        Page<Producto> expectedPage = new PageImpl<>(expectedProducts);


        when(productosRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(productoMapper.toProductResponse(any(Producto.class))).thenReturn(productoResponse1);

        // Act
        Page<ProductoResponse> actualPage = productoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
        System.out.println(actualPage.getTotalElements());
        // Assert
        assertAll("findAll",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0)
        );

        // Verify
        verify(productosRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(productoMapper, times(2)).toProductResponse(any(Producto.class));
    }

    @Test
    void findAll_ShouldReturnProductsByMarca_WhenMarcaParameterProvided() {
        // Arrange
        Optional<String> marca = Optional.of("nike");
        List<Producto> expectedProducts = List.of(producto2);
        List<ProductoResponse> expectedResponseProducts = List.of(productoResponse2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Producto> expectedPage = new PageImpl<>(expectedProducts);


        when(productosRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(productoMapper.toProductResponse(any(Producto.class))).thenReturn(productoResponse2);

        // Act
        Page<ProductoResponse> actualPage = productoService.findAll(marca, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertAll("findAllWithMarca",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0)
        );

        // Verify
        verify(productosRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(productoMapper, times(1)).toProductResponse(any(Producto.class));
    }

    @Test
    void findAll_ShouldReturnProductsByCategoria_WhenCategoriaParameterProvided() {
        // Arrange
        Optional<String> categoriaNombre = Optional.of("deportes");
        List<Producto> expectedProducts = List.of(producto2);
        List<ProductoResponse> expectedResponseProducts = List.of(productoResponse2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Producto> expectedPage = new PageImpl<>(expectedProducts);

        when(productosRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(productoMapper.toProductResponse(any(Producto.class))).thenReturn(productoResponse1);

        // Act
        Page<ProductoResponse> actualPage = productoService.findAll(Optional.empty(), categoriaNombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertAll("findAllWithCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0)
        );

        // Verify
        verify(productosRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(productoMapper, times(1)).toProductResponse(any(Producto.class));
    }

    @Test
    void findAll_ShouldReturnProductsByMarcaAndCategoria_WhenBothParametersProvided() {
        // Arrange
        Optional<String> marca = Optional.of("nike");
        Optional<String> categoriaNombre = Optional.of("deportes");
        List<Producto> expectedProducts = List.of(producto2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> expectedPage = new PageImpl<>(expectedProducts);

        when(productosRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(productoMapper.toProductResponse(any(Producto.class))).thenReturn(productoResponse2);

        // Act
        Page<ProductoResponse> actualPage = productoService.findAll(marca, categoriaNombre, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertAll("findAllWithMarcaAndCategoria",
                () -> assertNotNull(actualPage),
                () -> assertFalse(actualPage.isEmpty()),
                () -> assertTrue(actualPage.getTotalElements() > 0)
        );

        // Verify
        verify(productosRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(productoMapper, times(1)).toProductResponse(any(Producto.class));
    }

    @Test
    void findById_ShouldReturnProduct_WhenValidIdProvided() {
        // Arrange
        Long id = 1L;
        when(productosRepository.findById(id)).thenReturn(Optional.of(producto1));
        when(productoMapper.toProductResponse(producto1)).thenReturn(productoResponse1);

        // Act
        ProductoResponse actualProduct = productoService.findById(id);

        // Assert
        assertEquals(productoResponse1, actualProduct);

        // Verify
        verify(productosRepository, times(1)).findById(id);
        verify(productoMapper, times(1)).toProductResponse(producto1);
    }

    @Test
    void findById_ShouldThrowProductoNotFound_WhenInvalidIdProvided() {
        // Arrange
        Long id = 1L;
        when(productosRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var res = assertThrows(ProductoNotFound.class, () -> productoService.findById(id));
        assertEquals("Producto con id " + id + " no encontrado", res.getMessage());

        // Verify
        verify(productosRepository, times(1)).findById(id);
    }

    @Test
    void findByUuid_ShouldReturnProduct_WhenValidUuidProvided() {
        // Arrange
        UUID expectedUuid = producto1.getUuid();
        when(productosRepository.findByUuid(expectedUuid)).thenReturn(Optional.of(producto1));
        when(productoMapper.toProductResponse(producto1)).thenReturn(productoResponse1);

        // Act
        ProductoResponse actualProduct = productoService.findbyUuid(expectedUuid.toString());

        // Assert
        assertEquals(productoResponse1, actualProduct);

        // Verify
        verify(productosRepository, times(1)).findByUuid(expectedUuid);
        verify(productoMapper, times(1)).toProductResponse(producto1);
    }

    @Test
    void findByUuid_ShouldThrowProductoNotFound_WhenInvalidUuidProvided() {
        // Arrange
        String uuid = "98bb49a6-3ae5-4e50-a606-db397a8772bZ";

        // Act & Assert
        var res = assertThrows(ProductoBadUuid.class, () -> productoService.findbyUuid(uuid));
        assertEquals("UUID: " + uuid + " no válido o de formato incorrecto", res.getMessage());

        // Verify
        // verify(productosRepository, times(1)).findByUuid(UUID.fromString(uuid));
    }

    @Test
    void save_ShouldReturnSavedProduct_WhenValidProductCreateDtoProvided() throws IOException {
        // Arrange
        ProductoCreateRequest productoCreateRequest = ProductoCreateRequest.builder()
                .marca("Marca1")
                .modelo("Categoria1")
                .descripcion("Descripción1")
                .precio(100.0)
                .imagen("http://placeimg.com/640/480/people")
                .categoria("OTROS")
                .stock(5)
                .build();


        Producto expectedProduct = Producto.builder()
                .id(1L)
                .marca("Marca1")
                .modelo("Categoria1")
                .descripcion("Descripción1")
                .precio(100.0)
                .imagen("http://placeimg.com/640/480/people")
                .stock(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .isDeleted(false)
                .categoria(categoria)
                .build();

        ProductoResponse expectedProductResponse = ProductoResponse.builder()
                .id(1L)
                .marca("Marca1")
                .modelo("Categoria1")
                .descripcion("Descripción1")
                .precio(100.0)
                .imagen("http://placeimg.com/640/480/people")
                .stock(5)
                .categoria("OTROS")
                .build();


        when(categoriasRepository.findByNombreEqualsIgnoreCase(productoCreateRequest.getCategoria())).thenReturn(Optional.of(categoria));
        when(productoMapper.toProduct(productoCreateRequest, categoria)).thenReturn(expectedProduct);
        when(productosRepository.save(expectedProduct)).thenReturn(expectedProduct);
        when(productoMapper.toProductResponse(expectedProduct)).thenReturn(expectedProductResponse);
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        // Act
        ProductoResponse actualProduct = productoService.save(productoCreateRequest);

        // Assert
        assertEquals(expectedProductResponse, actualProduct);

        // Verify
        verify(categoriasRepository, times(1)).findByNombreEqualsIgnoreCase(productoCreateRequest.getCategoria());
        verify(productosRepository, times(1)).save(productoCaptor.capture());
        verify(productoMapper, times(1)).toProduct(productoCreateRequest, categoria);
        verify(productoMapper, times(1)).toProductResponse(expectedProduct);
    }

    @Test
    void save_BadRequestException_CategoriaNotFoundOrIsDeleted() throws IOException {
        // Arrange
        ProductoCreateRequest productoCreateRequest = ProductoCreateRequest.builder()
                .marca("Marca1")
                .modelo("Categoria1")
                .descripcion("Descripción1")
                .precio(100.0)
                .imagen("http://placeimg.com/640/480/people")
                .categoria("OTROS")
                .stock(5)
                .build();

        when(categoriasRepository.findByNombreEqualsIgnoreCase(productoCreateRequest.getCategoria())).thenReturn(Optional.empty());

        // Act & Assert
        var res = assertThrows(ProductoBadRequest.class, () -> productoService.save(productoCreateRequest));
        assertEquals("La categoría " + productoCreateRequest.getCategoria() + " no existe o está borrada", res.getMessage());

        // Verify
        verify(categoriasRepository, times(1)).findByNombreEqualsIgnoreCase(productoCreateRequest.getCategoria());
        verify(productosRepository, times(0)).save(any(Producto.class));
        verify(productoMapper, times(0)).toProduct(productoCreateRequest, categoria);
    }

    @Test
    void update_ShouldReturnUpdatedProduct_WhenValidIdAndProductUpdateDtoProvided() throws IOException {
        // Arrange
        Long id = 1L;
        ProductoUpdateRequest productoUpdateRequest = ProductoUpdateRequest.builder()
                .marca("Marca1")
                .modelo("Categoria1")
                .descripcion("Descripción1")
                .precio(100.0)
                .imagen("http://placeimg.com/640/480/people")
                .categoria("OTROS")
                .stock(5)
                .isDeleted(false)
                .build();

        Producto existingProduct = producto1;
        ProductoResponse expectedProductResponse = productoResponse1;

        when(productosRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        when(categoriasRepository.findByNombreEqualsIgnoreCase(productoUpdateRequest.getCategoria())).thenReturn(Optional.of(categoria));
        when(productosRepository.save(existingProduct)).thenReturn(existingProduct);
        when(productoMapper.toProduct(productoUpdateRequest, producto1, categoria)).thenReturn(existingProduct);
        when(productoMapper.toProductResponse(existingProduct)).thenReturn(expectedProductResponse);
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        // Act
        ProductoResponse actualProduct = productoService.update(id, productoUpdateRequest);

        // Assert
        assertEquals(expectedProductResponse, actualProduct);

        // Verify
        verify(productosRepository, times(1)).findById(id);
        verify(categoriasRepository, times(1)).findByNombreEqualsIgnoreCase(productoUpdateRequest.getCategoria());
        verify(productosRepository, times(1)).save(productoCaptor.capture());
        verify(productoMapper, times(1)).toProduct(productoUpdateRequest, producto1, categoria);
        verify(productoMapper, times(1)).toProductResponse(existingProduct);
    }

    @Test
    void update_ShouldThrowProductoNotFound_WhenInvalidIdProvided() {
        // Arrange
        Long id = 1L;
        ProductoUpdateRequest productoUpdateRequest = ProductoUpdateRequest.builder()
                .marca("Marca1")
                .modelo("Categoria1")
                .descripcion("Descripción1")
                .precio(100.0)
                .imagen("http://placeimg.com/640/480/people")
                .categoria("OTROS")
                .stock(5)
                .isDeleted(false)
                .build();

        when(productosRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var res = assertThrows(ProductoNotFound.class, () -> productoService.update(id, productoUpdateRequest));
        assertEquals("Producto con id " + id + " no encontrado", res.getMessage());

        // Verify
        verify(productosRepository, times(0)).save(any(Producto.class));
    }

    @Test
    void deleteById_ShouldDeleteProduct_WhenValidIdProvided() throws IOException {
        // Arrange
        Long id = 1L;
        Producto existingProduct = producto1;
        when(productosRepository.findById(id)).thenReturn(Optional.of(existingProduct));
        doNothing().when(webSocketHandlerMock).sendMessage(any());

        // Act
        productoService.deleteById(id);

        // Assert
        verify(productosRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_ShouldThrowProductoNotFound_WhenInvalidIdProvided() {
        // Arrange
        Long id = 1L;
        when(productosRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var res = assertThrows(ProductoNotFound.class, () -> productoService.deleteById(id));
        assertEquals("Producto con id " + id + " no encontrado", res.getMessage());

        // Verify
        verify(productosRepository, times(0)).deleteById(id);
    }

    @Test
    void onChange_ShouldSendMessage_WhenValidDataProvided() throws IOException {
        // Arrange
        doNothing().when(webSocketHandlerMock).sendMessage(any(String.class));

        // Act
        productoService.onChange(Notificacion.Tipo.CREATE, any(Producto.class));
    }

    @Test
    void updateImage_ShouldUpdateImageAndReturnProduct_WhenValidIdAndImageProvided() throws IOException {
        // Arrange
        String imageUrl = "http://placeimg.com/640/480/people";

        MultipartFile multipartFile = mock(MultipartFile.class);

        when(productosRepository.findById(producto1.getId())).thenReturn(Optional.of(producto1));
        when(storageService.store(multipartFile)).thenReturn(imageUrl);
        when(productosRepository.save(any(Producto.class))).thenReturn(producto1);
        when(productoMapper.toProductResponse(any(Producto.class))).thenReturn(productoResponse1);
        doNothing().when(webSocketHandlerMock).sendMessage(anyString());

        // Act
        ProductoResponse updatedProduct = productoService.updateImage(producto1.getId(), multipartFile, false);

        // Assert
        assertEquals(updatedProduct.getImagen(), imageUrl);
        verify(productosRepository, times(1)).save(any(Producto.class));
        verify(storageService, times(1)).delete(producto1.getImagen());
        verify(storageService, times(1)).store(multipartFile);
        verify(productoMapper, times(1)).toProductResponse(any(Producto.class));
    }
}
