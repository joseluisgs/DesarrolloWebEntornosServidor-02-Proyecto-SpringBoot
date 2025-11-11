package dev.joseluisgs.tiendaapidaw.rest.categorias.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.joseluisgs.tiendaapidaw.rest.categorias.dto.CategoriaRequest;
import dev.joseluisgs.tiendaapidaw.rest.categorias.exceptions.CategoriaConflict;
import dev.joseluisgs.tiendaapidaw.rest.categorias.exceptions.CategoriaNotFound;
import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapidaw.rest.categorias.services.CategoriasService;
import dev.joseluisgs.tiendaapidaw.utils.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class) // Extensión de Mockito para usarlo
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"}) // Usuario de prueba (admin, tiene de rol usaurio y admin)
class CategoriasRestControllerTest {
    private final String myEndpoint = "/v1/categorias";

    private final Categoria categoria1 = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "DEPORTES", LocalDateTime.now(), LocalDateTime.now(), false);
    private final Categoria categoria2 = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "ELECTRONICA", LocalDateTime.now(), LocalDateTime.now(), false);
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc; // Cliente MVC
    @MockBean
    private CategoriasService categoriasService;

    @Autowired
    public CategoriasRestControllerTest(CategoriasService categoriasService) {
        this.categoriasService = categoriasService;
        mapper.registerModule(new JavaTimeModule()); // Necesario para que funcione LocalDateTime
    }

    @Test
    @WithAnonymousUser
    void NotAuthenticated() throws Exception {
        // Localpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(403, response.getStatus());
    }


    @Test
    void getAllCategorias() throws Exception {
        var list = List.of(categoria1, categoria2);
        Page<Categoria> page = new PageImpl<>(list);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        // Arrange
        when(categoriasService.findAll(Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // System.out.println(response.getContentAsString());
        PageResponse<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll("findallCategorias",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        // Verify
        verify(categoriasService, times(1)).findAll(Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllCategoriasByNombre() throws Exception {
        var list = List.of(categoria1);
        var localEndpoint = myEndpoint + "?nombre=deportes";

        // Crear parámetros adicionales
        Optional<String> nombre = Optional.of("deportes");
        Optional<Boolean> booleano = Optional.empty(); // o Optional.of(true) o Optional.of(false), dependiendo de lo que necesites
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending()); // ejemplo de creación de un objeto Pageable
        Page<Categoria> page = new PageImpl<>(list);

        // Arrange
        when(categoriasService.findAll(nombre, booleano, pageable)).thenReturn(page);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(localEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(categoriasService, times(1)).findAll(nombre, booleano, pageable);
    }

    @Test
    void getCategoriaById() throws Exception {
        var myLocalEndpoint = myEndpoint + "/f58da854-f369-4539-81e3-42451a1f1f15";

        // Arrange
        when(categoriasService.findById(any(UUID.class))).thenReturn(categoria1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(), Categoria.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoria1, res)
        );

        // Verify
        verify(categoriasService, times(1)).findById(any(UUID.class));
    }

    @Test
    void getCategoriaByIdNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/f58da854-f369-4539-81e3-42451a1f1f15";

        // Arrange
        when(categoriasService.findById(any(UUID.class))).thenThrow(new CategoriaNotFound("f58da854-f369-4539-81e3-42451a1f1f15"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(categoriasService, times(1)).findById(any(UUID.class));
    }

    @Test
        // @WithUserDetails(value = "admin")
        // Usuario admin
    void createCategoria() throws Exception {
        var categoriaDto = new CategoriaRequest("TEST", false);

        // Arrange
        when(categoriasService.save(any(CategoriaRequest.class))).thenReturn(categoria1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(categoriaDto)))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(), Categoria.class);

        // Assert
        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(categoria1, res)
        );

        // Verify
        verify(categoriasService, times(1)).save(any(CategoriaRequest.class));
    }

    @Test
    void createCategoriaWithBadRequest() throws Exception {
        var categoriaDto = new CategoriaRequest("TE", false);


        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(categoriaDto)))
                .andReturn().getResponse();

        // System.out.println(response.getContentAsString());

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El nombre debe tener al menos 3 caracteres"))
        );
    }

    @Test
    void createCategoriaWithNombreExists() throws Exception {
        var categoriaDto = new CategoriaRequest("TEST", false);

        // Arrange
        when(categoriasService.save(any(CategoriaRequest.class))).thenThrow(new CategoriaConflict("Ya existe una categoría con el nombre " + categoriaDto.getNombre()));

        // Arrange
        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(categoriaDto)))
                .andReturn().getResponse();

        // System.out.println(response.getContentAsString());

        // Assert
        assertAll(
                () -> assertEquals(409, response.getStatus())
                // () -> assertTrue(response.getContentAsString().contains("Ya existe una categoría con el nombre " + categoriaDto.getNombre()))
        );
    }

    @Test
    void updateCategoria() throws Exception {
        var myLocalEndpoint = myEndpoint + "/f58da854-f369-4539-81e3-42451a1f1f15";
        var categoriaDto = new CategoriaRequest("TEST", false);

        // Arrange
        when(categoriasService.update(any(UUID.class), any(CategoriaRequest.class))).thenReturn(categoria1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(categoriaDto)))
                .andReturn().getResponse();

        Categoria res = mapper.readValue(response.getContentAsString(), Categoria.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoria1, res)
        );

        // Verify
        verify(categoriasService, times(1)).update(any(UUID.class), any(CategoriaRequest.class));
    }

    @Test
    void updateCategoriaNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/f58da854-f369-4539-81e3-42451a1f1f15";
        var categoriaDto = new CategoriaRequest("TEST", false);

        // Arrange
        when(categoriasService.update(any(UUID.class), any(CategoriaRequest.class))).thenThrow(new CategoriaNotFound("f58da854-f369-4539-81e3-42451a1f1f15"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(categoriaDto)))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());
    }

    @Test
    void updateProductWithBadRequest() throws Exception {
        var myLocalEndpoint = myEndpoint + "/f58da854-f369-4539-81e3-42451a1f1f15";
        var categoriaDto = new CategoriaRequest("TE", false);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(categoriaDto)))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus())
                // () -> assertTrue(response.getContentAsString().contains("El nombre debe tener al menos 3 caracteres"))
        );
    }

    @Test
    void updateCategoriaWithNombreExists() throws Exception {
        var myLocalEndpoint = myEndpoint + "/f58da854-f369-4539-81e3-42451a1f1f15";
        var categoriaDto = new CategoriaRequest("TEST", false);

        // Arrange
        when(categoriasService.update(any(UUID.class), any(CategoriaRequest.class))).thenThrow(new CategoriaConflict("Ya existe una categoría con el nombre " + categoriaDto.getNombre()));

        // Arrange
        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(categoriaDto)))
                .andReturn().getResponse();

        // System.out.println(response.getContentAsString());

        // Assert
        assertAll(
                () -> assertEquals(409, response.getStatus())
                // () -> assertTrue(response.getContentAsString().contains("Ya existe una categoría con el nombre " + categoriaDto.getNombre()))
        );
    }

    @Test
    void deleteCategoria() throws Exception {
        var myLocalEndpoint = myEndpoint + "/f58da854-f369-4539-81e3-42451a1f1f15";

        // Arrange
        doNothing().when(categoriasService).deleteById(any(UUID.class));

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
        verify(categoriasService, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void deleteCategoriaNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/f58da854-f369-4539-81e3-42451a1f1f15";

        // Arrange
        doThrow(new CategoriaNotFound("f58da854-f369-4539-81e3-42451a1f1f15")).when(categoriasService).deleteById(any(UUID.class));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertEquals(404, response.getStatus());

        // Verify
        verify(categoriasService, times(1)).deleteById(any(UUID.class));
    }
}