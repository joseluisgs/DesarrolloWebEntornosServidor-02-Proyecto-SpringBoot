package dev.joseluisgs.tiendaapidaw.rest.pedidos.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.exceptions.*;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Cliente;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Direccion;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.LineaPedido;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Pedido;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.services.PedidosService;
import dev.joseluisgs.tiendaapidaw.utils.pagination.PageResponse;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class) // Extensión de Mockito para usarlo
// Solo para admin por ahora
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"}) // Usuario de prueba (admin, tiene de rol usaurio y admin)
class PedidosRestControllerTest {
    private final String myEndpoint = "/v1/pedidos";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Pedido pedido1 = Pedido.builder()
            .id(new ObjectId("5f9f1a3b9d6b6d2e3c1d6f1a"))
            .idUsuario(1L)
            .cliente(
                    new Cliente("JoseLuisGS", "joseluisgs@soydev.dev", "1234567890",
                            new Direccion("Calle", "1", "Ciudad", "Provincia", "Pais", "12345")
                    )
            )
            .lineasPedido(List.of(LineaPedido.builder()
                    .idProducto(1L)
                    .cantidad(2)
                    .precioProducto(10.0)
                    .build()))
            .build();
    @Autowired
    MockMvc mockMvc; // Cliente MVC
    @MockBean
    private PedidosService pedidosService;

    @Autowired
    public PedidosRestControllerTest(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
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
    void getAllPedidos() throws Exception {
        var pedidosList = List.of(pedido1);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(pedidosList);

        // Arrange
        when(pedidosService.findAll(pageable)).thenReturn(page);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Pedido> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll("findall",
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(pedidosService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getPedidoById() throws Exception {
        // Arrange
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        when(pedidosService.findById(any(ObjectId.class))).thenReturn(pedido1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Pedido res = mapper.readValue(response.getContentAsString(), Pedido.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(pedido1, res)
        );

        // Verify
        verify(pedidosService, times(1)).findById(any(ObjectId.class));
    }

    @Test
    void getPedidoByIdNoFound() throws Exception {
        // Arrange
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        when(pedidosService.findById(any(ObjectId.class)))
                .thenThrow(new PedidoNotFound("5f9f1a3b9d6b6d2e3c1d6f1a"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        // Verify
        verify(pedidosService, times(1)).findById(any(ObjectId.class));
    }

    @Test
    void getPedidosByUsuario() throws Exception {
        // Arrange
        var myLocalEndpoint = myEndpoint + "/usuario/1";
        var pedidosList = List.of(pedido1);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(pedidosList);

        // Arrange
        when(pedidosService.findByIdUsuario(anyLong(), any(Pageable.class))).thenReturn(page);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Pedido> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        // Verify
        verify(pedidosService, times(1)).findByIdUsuario(anyLong(), any(Pageable.class));
    }

    @Test
    void createPedido() throws Exception {
        // Arrange
        when(pedidosService.save(any(Pedido.class))).thenReturn(pedido1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        Pedido res = mapper.readValue(response.getContentAsString(), Pedido.class);

        // Assert
        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(pedido1, res)
        );

        // Verify
        verify(pedidosService, times(1)).save(any(Pedido.class));
    }

    @Test
    void createPedidoNoItemsBadRequest() throws Exception {
        // Arrange
        when(pedidosService.save(any(Pedido.class))).thenThrow(new PedidoNotItems("5f9f1a3b9d6b6d2e3c1d6f1a"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        // Verify
        verify(pedidosService).save(any(Pedido.class));
    }

    @Test
    void createPedidoProductoBadPriceBadRequest() throws Exception {
        // Arrange
        when(pedidosService.save(any(Pedido.class))).thenThrow(new ProductoBadPrice(1L));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        // Verify
        verify(pedidosService).save(any(Pedido.class));
    }

    @Test
    void getPedidosProductoNotFoundBadRequest() throws Exception {
        // Arrange
        when(pedidosService.save(any(Pedido.class))).thenThrow(new ProductoNotFound(1L));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        // Verify
        verify(pedidosService).save(any(Pedido.class));
    }

    @Test
    void getPedidosProductoNotStockBadRequest() throws Exception {
        // Arrange
        when(pedidosService.save(any(Pedido.class))).thenThrow(new ProductoNotStock(1L));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        // Verify
        verify(pedidosService).save(any(Pedido.class));
    }

    @Test
    void updateProduct() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        when(pedidosService.update(any(ObjectId.class), any(Pedido.class))).thenReturn(pedido1);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        Pedido res = mapper.readValue(response.getContentAsString(), Pedido.class);

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(pedido1, res)
        );

        // Verify
        verify(pedidosService, times(1)).update(any(ObjectId.class), any(Pedido.class));
    }

    @Test
    void updatePedidoNoFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        when(pedidosService.update(any(ObjectId.class), any(Pedido.class)))
                .thenThrow(new PedidoNotFound("5f9f1a3b9d6b6d2e3c1d6f1a"));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                // Le paso el body
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        // Verify
        verify(pedidosService, times(1)).update(any(ObjectId.class), any(Pedido.class));
    }

    // Habría que testear casi lo mismo en el save con el update

    @Test
    void deletePedido() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        doNothing().when(pedidosService).delete(any(ObjectId.class));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(204, response.getStatus())
        );

        // Verify
        verify(pedidosService, times(1)).delete(any(ObjectId.class));
    }

    @Test
    void deletePedidoNoFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        // Arrange
        doThrow(new PedidoNotFound("5f9f1a3b9d6b6d2e3c1d6f1a")).when(pedidosService).delete(any(ObjectId.class));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Assert
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        // Verify
        verify(pedidosService, times(1)).delete(any(ObjectId.class));
    }
}