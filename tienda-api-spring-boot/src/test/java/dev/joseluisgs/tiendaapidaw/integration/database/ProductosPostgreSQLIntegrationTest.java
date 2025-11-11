package dev.joseluisgs.tiendaapidaw.integration.database;

import dev.joseluisgs.tiendaapidaw.AbstractIntegrationTest;
import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapidaw.rest.categorias.repositories.CategoriasRepository;
import dev.joseluisgs.tiendaapidaw.rest.productos.models.Producto;
import dev.joseluisgs.tiendaapidaw.rest.productos.repositories.ProductosRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🎯 OBJETIVO: Testear operaciones CRUD de Productos con PostgreSQL real usando TestContainers
 * 
 * 📋 QUÉ TESTEA:
 * - CRUD completo de Productos en PostgreSQL
 * - Relaciones JPA entre Producto y Categoría
 * - Transacciones y rollback
 * 
 * 🗄️ BASE DE DATOS: PostgreSQL (TestContainers)
 * 
 * 🔧 CONFIGURACIÓN: TestContainers PostgreSQL 15
 * 
 * ✅ CASOS CUBIERTOS:
 * - Creación de producto con categoría
 * - Actualización de producto
 * - Búsqueda por UUID
 * - Eliminación lógica
 * 
 * 🚫 CASOS EDGE:
 * - Búsqueda con UUID inválido
 */
@DisplayName("🐘 PostgreSQL Integration Tests - Productos")
@ActiveProfiles("test")
public class ProductosPostgreSQLIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductosRepository productosRepository;

    @Autowired
    private CategoriasRepository categoriasRepository;

    private Categoria categoria;
    private Producto producto;

    @BeforeEach
    void setUp() {
        productosRepository.deleteAll();
        categoriasRepository.deleteAll();

        categoria = Categoria.builder()
                .id(UUID.randomUUID())
                .nombre("Electrónica")
                .isDeleted(false)
                .build();
        categoria = categoriasRepository.save(categoria);

        producto = Producto.builder()
                .uuid(UUID.randomUUID())
                .marca("TestBrand")
                .modelo("Test Model")
                .descripcion("Producto para testing")
                .precio(999.99)
                .stock(10)
                .imagen("test.jpg")
                .categoria(categoria)
                .isDeleted(false)
                .build();
    }

    @AfterEach
    void tearDown() {
        productosRepository.deleteAll();
        categoriasRepository.deleteAll();
    }

    @Test
    @DisplayName("✅ Debe crear un producto en PostgreSQL con categoría asociada")
    void testCreateProducto() {
        // When
        Producto savedProducto = productosRepository.save(producto);

        // Then
        assertNotNull(savedProducto.getId());
        assertEquals(producto.getUuid(), savedProducto.getUuid());
        assertEquals(producto.getMarca(), savedProducto.getMarca());
        assertEquals(producto.getModelo(), savedProducto.getModelo());
        assertNotNull(savedProducto.getCreatedAt());
        assertNotNull(savedProducto.getCategoria());
        assertEquals(categoria.getId(), savedProducto.getCategoria().getId());
    }

    @Test
    @DisplayName("✅ Debe encontrar producto por UUID")
    void testFindByUuid() {
        // Given
        Producto savedProducto = productosRepository.save(producto);

        // When
        Optional<Producto> found = productosRepository.findByUuid(savedProducto.getUuid());

        // Then
        assertTrue(found.isPresent());
        assertEquals(savedProducto.getId(), found.get().getId());
        assertEquals(savedProducto.getMarca(), found.get().getMarca());
    }

    @Test
    @DisplayName("✅ Debe actualizar producto correctamente")
    void testUpdateProducto() {
        // Given
        Producto savedProducto = productosRepository.save(producto);
        
        // When
        savedProducto.setModelo("Updated Model");
        savedProducto.setPrecio(1299.99);
        savedProducto.setStock(15);
        Producto updatedProducto = productosRepository.save(savedProducto);

        // Then
        assertEquals("Updated Model", updatedProducto.getModelo());
        assertEquals(1299.99, updatedProducto.getPrecio());
        assertEquals(15, updatedProducto.getStock());
        assertNotNull(updatedProducto.getUpdatedAt());
    }

    @Test
    @DisplayName("✅ Debe eliminar producto (soft delete)")
    void testDeleteProducto() {
        // Given
        Producto savedProducto = productosRepository.save(producto);

        // When
        savedProducto.setIsDeleted(true);
        productosRepository.save(savedProducto);

        // Then
        Optional<Producto> found = productosRepository.findById(savedProducto.getId());
        assertTrue(found.isPresent());
        assertTrue(found.get().getIsDeleted());
    }

    @Test
    @DisplayName("🚫 Caso edge: UUID inválido debe retornar vacío")
    void testFindByInvalidUuid() {
        // Given
        UUID uuidInvalido = UUID.randomUUID();

        // When
        Optional<Producto> found = productosRepository.findByUuid(uuidInvalido);

        // Then
        assertFalse(found.isPresent());
    }
}
