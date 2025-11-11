package dev.joseluisgs.tiendaapidaw.integration.database;

import dev.joseluisgs.tiendaapidaw.AbstractIntegrationTest;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Cliente;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Direccion;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.LineaPedido;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Pedido;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.repositories.PedidosRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🎯 OBJETIVO: Testear operaciones CRUD de Pedidos con MongoDB real usando TestContainers
 * 
 * 📋 QUÉ TESTEA:
 * - CRUD completo de Pedidos en MongoDB
 * - Serialización/deserialización de ObjectId
 * - Documentos embebidos (Cliente, Dirección, Líneas de Pedido)
 * - Cálculos automáticos de totales
 * 
 * 🗄️ BASE DE DATOS: MongoDB (TestContainers)
 * 
 * 🔧 CONFIGURACIÓN: TestContainers MongoDB 5.0
 * 
 * ✅ CASOS CUBIERTOS:
 * - Creación de pedido con líneas
 * - Actualización de pedido
 * - Búsqueda por ObjectId
 * - Cálculo automático de totales
 * - Serialización JSON de ObjectId
 * 
 * 🚫 CASOS EDGE:
 * - ObjectId inválido en búsqueda
 */
@DisplayName("🍃 MongoDB Integration Tests - Pedidos")
@ActiveProfiles("test")
public class PedidosMongoDBIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PedidosRepository pedidosRepository;

    private Pedido pedido;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        pedidosRepository.deleteAll();

        Direccion direccion = Direccion.builder()
                .calle("Calle Principal")
                .numero("123")
                .ciudad("Madrid")
                .provincia("Madrid")
                .pais("España")
                .codigoPostal("28001")
                .build();

        cliente = new Cliente(
                "Juan Pérez",
                "juan.perez@example.com",
                "600123456",
                direccion
        );

        List<LineaPedido> lineas = new ArrayList<>();
        lineas.add(LineaPedido.builder()
                .idProducto(1L)
                .precioProducto(100.0)
                .cantidad(2)
                .total(200.0)
                .build());
        lineas.add(LineaPedido.builder()
                .idProducto(2L)
                .precioProducto(50.0)
                .cantidad(1)
                .total(50.0)
                .build());

        pedido = Pedido.builder()
                .id(new ObjectId())
                .idUsuario(1L)
                .cliente(cliente)
                .build();
        pedido.setLineasPedido(lineas);
    }

    @AfterEach
    void tearDown() {
        pedidosRepository.deleteAll();
    }

    @Test
    @DisplayName("✅ Debe crear un pedido en MongoDB con ObjectId generado")
    void testCreatePedido() {
        // When
        Pedido savedPedido = pedidosRepository.save(pedido);

        // Then
        assertNotNull(savedPedido.getId());
        assertNotNull(savedPedido.get_id());
        assertTrue(savedPedido.get_id().matches("[a-f0-9]{24}"));
        assertEquals(pedido.getIdUsuario(), savedPedido.getIdUsuario());
        assertEquals(pedido.getCliente(), savedPedido.getCliente());
        assertNotNull(savedPedido.getCreatedAt());
    }

    @Test
    @DisplayName("✅ Debe calcular automáticamente totalItems y total desde líneas de pedido")
    void testAutoCalculateTotals() {
        // When
        Pedido savedPedido = pedidosRepository.save(pedido);

        // Then
        assertEquals(2, savedPedido.getTotalItems());
        assertEquals(250.0, savedPedido.getTotal());
    }

    @Test
    @DisplayName("✅ Debe encontrar pedido por ObjectId")
    void testFindByObjectId() {
        // Given
        Pedido savedPedido = pedidosRepository.save(pedido);

        // When
        Optional<Pedido> found = pedidosRepository.findById(savedPedido.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(savedPedido.getId(), found.get().getId());
        assertEquals(savedPedido.getCliente().nombreCompleto(), 
                     found.get().getCliente().nombreCompleto());
    }

    @Test
    @DisplayName("✅ Debe actualizar pedido correctamente")
    void testUpdatePedido() {
        // Given
        Pedido savedPedido = pedidosRepository.save(pedido);
        
        // When - Agregar una línea más
        List<LineaPedido> nuevasLineas = new ArrayList<>(savedPedido.getLineasPedido());
        nuevasLineas.add(LineaPedido.builder()
                .idProducto(3L)
                .precioProducto(75.0)
                .cantidad(1)
                .total(75.0)
                .build());
        savedPedido.setLineasPedido(nuevasLineas);
        Pedido updatedPedido = pedidosRepository.save(savedPedido);

        // Then
        assertEquals(3, updatedPedido.getTotalItems());
        assertEquals(325.0, updatedPedido.getTotal());
    }

    @Test
    @DisplayName("✅ Debe serializar ObjectId como String en JSON")
    void testObjectIdSerializationToJson() {
        // When
        Pedido savedPedido = pedidosRepository.save(pedido);
        String hexString = savedPedido.get_id();

        // Then
        assertNotNull(hexString);
        assertEquals(24, hexString.length());
        assertTrue(hexString.matches("[a-f0-9]{24}"));
        
        ObjectId reconstructed = new ObjectId(hexString);
        assertEquals(savedPedido.getId(), reconstructed);
    }

    @Test
    @DisplayName("🚫 Caso edge: ObjectId inválido debe retornar vacío")
    void testFindByInvalidObjectId() {
        // Given
        ObjectId invalidId = new ObjectId();

        // When
        Optional<Pedido> found = pedidosRepository.findById(invalidId);

        // Then
        assertFalse(found.isPresent());
    }
}
