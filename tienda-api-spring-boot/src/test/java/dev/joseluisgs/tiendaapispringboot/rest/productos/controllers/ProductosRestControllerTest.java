package dev.joseluisgs.tiendaapispringboot.rest.productos.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.joseluisgs.tiendaapispringboot.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapispringboot.rest.productos.dto.ProductoCreateRequest;
import dev.joseluisgs.tiendaapispringboot.rest.productos.dto.ProductoResponse;
import dev.joseluisgs.tiendaapispringboot.rest.productos.dto.ProductoUpdateRequest;
import dev.joseluisgs.tiendaapispringboot.rest.productos.exceptions.ProductoNotFound;
import dev.joseluisgs.tiendaapispringboot.rest.productos.models.Producto;
import dev.joseluisgs.tiendaapispringboot.rest.productos.services.ProductosService;
import dev.joseluisgs.tiendaapispringboot.utils.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class) // Extensión de Mockito para usarlo
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"}) // Usuario de prueba (admin, tiene de rol usaurio y admin)
class ProductosRestControllerTest {
    private final String myEndpoint = "/v1/productos";

    private final Categoria categoria = Categoria.builder()
            .id(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"))
            .nombre("DEPORTES")
            .build();
    private final Producto producto1 = Producto.builder()
            .id(1L)
            .marca("Adidas")
            .modelo("Zapatillas")
            .descripcion("Zapatillas de deporte")
            .precio(100.0)
            .imagen("http://placeimg.com/640/480/people")
            .stock(5)
            .categoria(categoria)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("80e559b5-83c5-4555-ba0b-bb9fddb6e96c"))
            .build();

    private final Producto producto2 = Producto.builder()
            .id(2L)
            .marca("Nike")
            .modelo("Zapatillas")
            .descripcion("Zapatillas de deporte")
            .precio(100.0)
            .imagen("http://placeimg.com/640/480/people")
            .stock(5)
            .categoria(categoria)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("542f0a0b-064b-4022-b528-3b59f8bae821"))
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
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("80e559b5-83c5-4555-ba0b-bb9fddb6e96c"))
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
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("80e559b5-83c5-4555-ba0b-bb9fddb6e96c"))
            .build();


    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc; // Cliente MVC
    @MockitoBean
    private ProductosService productosService;

    @Autowired
    public ProductosRestControllerTest(ProductosService productosService) {
        this.productosService = productosService;
        mapper.registerModule(new JavaTimeModule()); // Necesario para que funcione LocalDateTime
    }


    @Test
    void getAllProducts() throws Exception {
        var productosList = List.of(productoResponse1, productoResponse2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(productosList);

        // Arrange
        when(productosService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<ProductoResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        // Verify
        verify(productosService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);


    }

    @Test
    void getAllProductsByMarca() throws Exception {
        var productosList = List.of(productoResponse2);
        var localEndpoint = myEndpoint + "?marca=nike";

        Optional<String> marca = Optional.of("nike");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(productosList);

        // Arrange
        when(productosService.findAll(marca, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<ProductoResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll("findallByMarca",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(productosService, times(1)).findAll(marca, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllProductsByCategoria() throws Exception {
        var productosList = List.of(productoResponse2);
        var localEndpoint = myEndpoint + "?categoria=DEPORTES";

        Optional<String> categoria = Optional.of("DEPORTES");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(productosList);

        // Arrange
        when(productosService.findAll(Optional.empty(), categoria, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<ProductoResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll("findallByCategoria",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(productosService, times(1)).findAll(Optional.empty(), categoria, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllProductsByMarcaAndCategoria() throws Exception {
        var productosList = List.of(productoResponse2);
        var localEndpoint = myEndpoint + "?marca=nike&categoria=DEPORTES";

        Optional<String> marca = Optional.of("nike");
        Optional<String> categoria = Optional.of("DEPORTES");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(productosList);

        // Arrange
        when(productosService.findAll(marca, categoria, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<ProductoResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll("findallByMarcaAndCategoria",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(productosService, times(1)).findAll(marca, categoria, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getProductById() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        when(productosService.findById(anyLong())).thenReturn(productoResponse1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ProductoResponse res = mapper.readValue(response.getContentAsString(), ProductoResponse.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(productoResponse1, res)
        );

        // Verify
        verify(productosService, times(1)).findById(anyLong());
    }

    @Test
    void getProductByIdNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        when(productosService.findById(anyLong())).thenThrow(new ProductoNotFound(1L));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(productosService, times(1)).findById(anyLong());
    }

    @Test
    void createProduct() throws Exception {
        var productoDto = ProductoCreateRequest.builder()
                .marca("Adidas")
                .modelo("Zapatillas")
                .descripcion("Zapatillas de deporte")
                .precio(100.0)
                .imagen("http://placeimg.com/640/480/people")
                .categoria("OTROS")
                .stock(5)
                .build();

        // Arrange
        when(productosService.save(any(ProductoCreateRequest.class))).thenReturn(productoResponse1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(productoDto)))
                .andReturn().getResponse();

        ProductoResponse res = mapper.readValue(response.getContentAsString(), ProductoResponse.class);

        // Assert
        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(productoResponse1, res)
        );

        // Verify
        verify(productosService, times(1)).save(any(ProductoCreateRequest.class));
    }

    @Test
    void createProductWithBadRequest() throws Exception {
        var productoDto = ProductoCreateRequest.builder()
                .marca("Ad")
                .modelo("")
                .descripcion("Zapatillas de deporte")
                .precio(-100.0)
                .imagen("http://placeimg.com/640/480/people")
                .categoria("OTROS")
                .stock(-5)
                .build();

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(productoDto)))
                .andReturn().getResponse();

        // System.out.println(response.getContentAsString());

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El precio no puede ser negativo")),
                () -> assertTrue(response.getContentAsString().contains("El nombre debe tener al menos 3 caracteres")),
                () -> assertTrue(response.getContentAsString().contains("El modelo no puede estar vacÃ\u00ADo")),
                () -> assertTrue(response.getContentAsString().contains("El stock no puede ser negativo"))
        );


    }

    @Test
    void updateProduct() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var productoDto = ProductoUpdateRequest.builder()
                .marca("Adidas")
                .modelo("Zapatillas")
                .descripcion("Zapatillas de deporte")
                .precio(100.0)
                .imagen("http://placeimg.com/640/480/people")
                .categoria("OTROS")
                .stock(5)
                .isDeleted(false)
                .build();

        // Arrange
        when(productosService.update(anyLong(), any(ProductoUpdateRequest.class))).thenReturn(productoResponse1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(productoDto)))
                .andReturn().getResponse();

        ProductoResponse res = mapper.readValue(response.getContentAsString(), ProductoResponse.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(productoResponse1, res)
        );

        // Verify
        verify(productosService, times(1)).update(anyLong(), any(ProductoUpdateRequest.class));
    }

    @Test
    void updateProductNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var productoDto = ProductoUpdateRequest.builder()
                .marca("Adidas")
                .modelo("Zapatillas")
                .descripcion("Zapatillas de deporte")
                .precio(100.0)
                .imagen("http://placeimg.com/640/480/people")
                .categoria("OTROS")
                .stock(5)
                .isDeleted(false)
                .build();

        // Arrange
        when(productosService.update(anyLong(), any(ProductoUpdateRequest.class))).thenThrow(new ProductoNotFound(1L));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(productoDto)))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());
    }

    @Test
    void updateProductWithBadRequest() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var productoDto = ProductoUpdateRequest.builder()
                .marca("Ad")
                .modelo("")
                .descripcion("Zapatillas de deporte")
                .precio(-100.0)
                .imagen("http://placeimg.com/640/480/people")
                .categoria("OTROS")
                .stock(-5)
                .isDeleted(false)
                .build();

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(productoDto)))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El precio no puede ser negativo")),
                () -> assertTrue(response.getContentAsString().contains("El nombre debe tener al menos 3 caracteres")),
                () -> assertTrue(response.getContentAsString().contains("El stock no puede ser negativo"))
        );
    }

    @Test
    void updatePartialProduct() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var productoDto = ProductoUpdateRequest.builder()
                .marca("Adidas")
                .imagen("http://placeimg.com/640/480/people")
                .categoria("OTROS")
                .stock(5)
                .isDeleted(false)
                .build();

        // Arrange
        when(productosService.update(anyLong(), any(ProductoUpdateRequest.class))).thenReturn(productoResponse1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        patch(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(productoDto)))
                .andReturn().getResponse();

        ProductoResponse res = mapper.readValue(response.getContentAsString(), ProductoResponse.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(productoResponse1, res)
        );

        // Verify
        verify(productosService, times(1)).update(anyLong(), any(ProductoUpdateRequest.class));
    }

    @Test
    void deleteProduct() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        doNothing().when(productosService).deleteById(anyLong());

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(204, response.getStatus())
        );

        // Verify
        verify(productosService, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteProductNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";

        // Arrange
        doThrow(new ProductoNotFound(1L)).when(productosService).deleteById(anyLong());

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(productosService, times(1)).deleteById(anyLong());
    }

    @Test
    void updateProductImage() throws Exception {
        var myLocalEndpoint = myEndpoint + "/imagen/1";

        // Arrange
        when(productosService.updateImage(anyLong(), any(MultipartFile.class), anyBoolean())).thenReturn(productoResponse1);

        // Crear un archivo simulado
        MockMultipartFile file = new MockMultipartFile(
                "file", // Nombre del parámetro del archivo en el controlador
                "filename.jpg", // Nombre del archivo
                MediaType.IMAGE_JPEG_VALUE, // Tipo de contenido del archivo
                "contenido del archivo".getBytes() // Contenido del archivo
        );

        // Crear una solicitud PATCH multipart con el fichero simulado
        MockHttpServletResponse response = mockMvc.perform(
                multipart(myLocalEndpoint)
                        .file(file)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
        ).andReturn().getResponse();


        ProductoResponse res = mapper.readValue(response.getContentAsString(), ProductoResponse.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(productoResponse1, res)
        );

        // Verify
        verify(productosService, times(1)).updateImage(anyLong(), any(MultipartFile.class), anyBoolean());
    }

}