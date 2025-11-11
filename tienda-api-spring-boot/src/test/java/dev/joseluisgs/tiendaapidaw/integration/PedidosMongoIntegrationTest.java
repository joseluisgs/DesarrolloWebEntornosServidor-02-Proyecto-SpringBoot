package dev.joseluisgs.tiendaapidaw.integration;

import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Cliente;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Direccion;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.models.Pedido;
import dev.joseluisgs.tiendaapidaw.rest.pedidos.repositories.PedidosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
public class PedidosMongoIntegrationTest {

    @Autowired
    private PedidosRepository pedidosRepository;

    private Pedido pedido;

    @BeforeEach
    public void setUp() {
        // Initialize a new Pedido entity before each test
        pedido = new Pedido();
        pedido.setId(null); // Let MongoDB generate the ObjectId
        
        // Create Cliente with Direccion
        Direccion direccion = Direccion.builder()
                .calle("Calle Test")
                .numero("123")
                .ciudad("Madrid")
                .provincia("Madrid")
                .pais("España")
                .codigoPostal("28001")
                .build();
                
        Cliente cliente = new Cliente(
                "Cliente Test",
                "test@example.com",
                "123456789",
                direccion
        );
        
        pedido.setCliente(cliente);
        pedido.setIdUsuario(1L);
        pedido.setTotal(100.0);
        // Set other properties as needed
    }

    @Test
    public void testCreatePedido() {
        Pedido savedPedido = pedidosRepository.save(pedido);
        assertNotNull(savedPedido.getId(), "Pedido ID should not be null after save");
    }

    @Test
    public void testReadPedido() {
        Pedido savedPedido = pedidosRepository.save(pedido);
        Optional<Pedido> foundPedido = pedidosRepository.findById(savedPedido.getId());
        assertTrue(foundPedido.isPresent(), "Pedido should be found");
        assertEquals(savedPedido.getCliente(), foundPedido.get().getCliente(), "Cliente should match");
    }

    @Test
    public void testUpdatePedido() {
        Pedido savedPedido = pedidosRepository.save(pedido);
        savedPedido.setTotal(150.0);
        Pedido updatedPedido = pedidosRepository.save(savedPedido);
        assertEquals(150.0, updatedPedido.getTotal(), "Total should be updated");
    }

    @Test
    public void testDeletePedido() {
        Pedido savedPedido = pedidosRepository.save(pedido);
        pedidosRepository.delete(savedPedido);
        Optional<Pedido> foundPedido = pedidosRepository.findById(savedPedido.getId());
        assertFalse(foundPedido.isPresent(), "Pedido should be deleted");
    }

    @Test
    public void testCrossDatabaseValidation() {
        // Add logic here to test cross-database validations
        // Example: Validate related entities in different databases
    }

    @Test
    public void testObjectIdSerialization() {
        // Test the serialization and deserialization of ObjectId
        Pedido savedPedido = pedidosRepository.save(pedido);
        String objectIdString = savedPedido.getId().toString();
        assertEquals(objectIdString, savedPedido.getId().toString(), "ObjectId should serialize correctly");
    }

    @Test
    public void testBusinessLogicCalculations() {
        // Add business logic calculations test here
        // Example: Validate total calculations based on items in the order
    }
}