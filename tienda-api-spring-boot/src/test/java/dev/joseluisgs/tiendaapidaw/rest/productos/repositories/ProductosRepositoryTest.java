package dev.joseluisgs.tiendaapidaw.rest.productos.repositories;

import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapidaw.rest.productos.models.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// Vamos a probar el repositorio, pero moqueamos la base de datos JPA
@DataJpaTest
class ProductosRepositoryTest {
    //Damos por hecho que funciona bien, ya que es un repositorio de Spring Data JPA
    private final Categoria categoria = new Categoria(null, "TEST", LocalDateTime.now(), LocalDateTime.now(), false);

    private final Producto producto1 = Producto.builder()
            .marca("Adidas")
            .modelo("Zapatillas")
            .descripcion("Zapatillas de deporte")
            .precio(100.0)
            .imagen("http://placeimg.com/640/480/people")
            .stock(5)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("80e559b5-83c5-4555-ba0b-bb9fddb6e96c"))
            .isDeleted(false)
            .categoria(categoria)
            .build();

    private final Producto producto2 = Producto.builder()
            .marca("Nike")
            .modelo("Zapatillas")
            .descripcion("Zapatillas de deporte")
            .precio(100.0)
            .imagen("http://placeimg.com/640/480/people")
            .stock(5)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .uuid(UUID.fromString("542f0a0b-064b-4022-b528-3b59f8bae821"))
            .isDeleted(false)
            .categoria(categoria)
            .build();

    @Autowired
    private ProductosRepository repository;
    @Autowired
    private TestEntityManager entityManager; // EntityManager para hacer las pruebas

    @BeforeEach
    void setUp() {
        // Vamos a salvar una categoria
        entityManager.merge(categoria);
        entityManager.flush();
        // Vamos a salvar dos productos
        entityManager.merge(producto1);
        entityManager.merge(producto2);
        entityManager.flush();
    }

    @Test
    void findAll() {
        // Act
        List<Producto> productos = repository.findAll();

        // Ten cuidado si estas usando datos en el script de test, ya que si no se borran, se acumulan

        // Assert
        assertAll("findAll",
                () -> assertNotNull(productos),
                () -> assertFalse(productos.isEmpty()),
                () -> assertTrue(productos.size() >= 2)
        );
    }


    @Test
    void findById_existingId_returnsOptionalWithProducto() {
        // Act
        Optional<Producto> optionalProducto = repository.findById(producto1.getId());

        // Assert
        assertAll("findById_existingId_returnsOptionalWithProducto",
                () -> assertNotNull(optionalProducto),
                () -> assertTrue(optionalProducto.isPresent()),
                () -> assertEquals(producto1.getId(), optionalProducto.get().getId())
        );
    }

    @Test
    void findById_nonExistingId_returnsEmptyOptional() {
        // Act
        Long id = -999L;
        Optional<Producto> optionalProducto = repository.findById(id);

        // Assert
        assertAll("findById_nonExistingId_returnsEmptyOptional",
                () -> assertNotNull(optionalProducto),
                () -> assertTrue(optionalProducto.isEmpty())
        );
    }

    @Test
    void findByUuid_existingUuid_returnsOptionalWithProducto() {
        // Act
        UUID uuid = UUID.fromString("542f0a0b-064b-4022-b528-3b59f8bae821");
        Optional<Producto> optionalProducto = repository.findByUuid(uuid);

        // Assert
        assertAll("findByUuid_existingUuid_returnsOptionalWithProducto",
                () -> assertNotNull(optionalProducto),
                () -> assertTrue(optionalProducto.isPresent()),
                () -> assertEquals(uuid, optionalProducto.get().getUuid())
        );
    }

    @Test
    void findByUuid_nonExistingUuid_returnsEmptyOptional() {
        // Act
        UUID uuid = UUID.fromString("98bb49a6-3ae5-4e50-a606-db397a8772b2");
        Optional<Producto> optionalProducto = repository.findByUuid(uuid);

        // Assert
        assertAll("findByUuid_nonExistingUuid_returnsEmptyOptional",
                () -> assertNotNull(optionalProducto),
                () -> assertTrue(optionalProducto.isEmpty())
        );
    }

    @Test
    void existsById_existingId_returnsTrue() {
        // Act
        boolean exists = repository.existsById(producto1.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        // Act
        Long id = -999L;
        boolean exists = repository.existsById(id);

        // Assert
        assertFalse(exists);
    }


    @Test
    void saveButNotExists() {
        // Arrange
        Producto producto = Producto.builder()
                .marca("New Brand")
                .modelo("New Model")
                .descripcion("New Description")
                .precio(200.0)
                .imagen("http://placeimg.com/640/480/people")
                .stock(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .isDeleted(false)
                .categoria(categoria)
                .build();

        // Act
        Producto savedProducto = repository.save(producto);
        var all = repository.findAll();

        // Assert
        assertAll("save",
                () -> assertNotNull(savedProducto),
                () -> assertTrue(repository.existsById(savedProducto.getId())),
                () -> assertTrue(all.size() >= 2)
        );
    }

    @Test
    void saveExists() {
        // Arrange
        Producto producto = Producto.builder()
                .marca("New Brand")
                .modelo("New Model")
                .descripcion("New Description")
                .precio(200.0)
                .imagen("http://placeimg.com/640/480/people")
                .stock(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .isDeleted(false)
                .categoria(categoria)
                .build();

        // Act
        Producto savedProducto = repository.save(producto);
        var all = repository.findAll();

        // Assert
        assertAll("save",
                () -> assertNotNull(savedProducto),
                () -> assertTrue(repository.existsById(producto.getId())),
                () -> assertTrue(all.size() >= 2)
        );
    }

    @Test
    void deleteById_existingId() {
        // Act
        repository.deleteById(producto1.getId());
        var all = repository.findAll();

        // Assert
        assertAll("deleteById_existingId",
                () -> assertFalse(repository.existsById(producto1.getId())),
                () -> assertFalse(all.isEmpty())
        );
    }

}
