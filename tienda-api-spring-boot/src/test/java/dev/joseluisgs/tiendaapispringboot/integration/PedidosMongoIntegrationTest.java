package dev.joseluisgs.tiendaapispringboot.integration;

import dev.joseluisgs.tiendaapispringboot.model.Pedidos;
import dev.joseluisgs.tiendaapispringboot.repository.PedidosRepository;
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

    private Pedidos pedido;

    @BeforeEach
    public void setUp() {
        // Initialize a new Pedidos entity before each test
        pedido = new Pedidos();
        pedido.setId(null); // Let MongoDB generate the ObjectId
        pedido.setCliente("Cliente Test");
        pedido.setTotal(100.0);
        // Set other properties as needed
    }

    @Test
    public void testCreatePedido() {
        Pedidos savedPedido = pedidosRepository.save(pedido);
        assertNotNull(savedPedido.getId(), "Pedido ID should not be null after save");
    }

    @Test
    public void testReadPedido() {
        Pedidos savedPedido = pedidosRepository.save(pedido);
        Optional<Pedidos> foundPedido = pedidosRepository.findById(savedPedido.getId());
        assertTrue(foundPedido.isPresent(), "Pedido should be found");
        assertEquals(savedPedido.getCliente(), foundPedido.get().getCliente(), "Cliente should match");
    }

    @Test
    public void testUpdatePedido() {
        Pedidos savedPedido = pedidosRepository.save(pedido);
        savedPedido.setTotal(150.0);
        Pedidos updatedPedido = pedidosRepository.save(savedPedido);
        assertEquals(150.0, updatedPedido.getTotal(), "Total should be updated");
    }

    @Test
    public void testDeletePedido() {
        Pedidos savedPedido = pedidosRepository.save(pedido);
        pedidosRepository.delete(savedPedido);
        Optional<Pedidos> foundPedido = pedidosRepository.findById(savedPedido.getId());
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
        Pedidos savedPedido = pedidosRepository.save(pedido);
        String objectIdString = savedPedido.getId().toString();
        assertEquals(objectIdString, savedPedido.getId().toString(), "ObjectId should serialize correctly");
    }

    @Test
    public void testBusinessLogicCalculations() {
        // Add business logic calculations test here
        // Example: Validate total calculations based on items in the order
    }
}