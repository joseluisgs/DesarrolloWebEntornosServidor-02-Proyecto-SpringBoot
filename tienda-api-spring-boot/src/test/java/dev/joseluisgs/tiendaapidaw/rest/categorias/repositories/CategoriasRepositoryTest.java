package dev.joseluisgs.tiendaapidaw.rest.categorias.repositories;

import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class CategoriasRepositoryTest {
    //Damos por hecho que funciona bien, ya que es un repositorio de Spring Data JPA
    private final Categoria categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), false);

    @Autowired
    private CategoriasRepository repository;
    @Autowired
    private TestEntityManager entityManager; // EntityManager para hacer las pruebas

    @BeforeEach
    void setUp() {
        // Vamos a salvar una categoria
        entityManager.merge(categoria);
        entityManager.flush();
    }

    @Test
    void findAll() {
        // Act
        List<Categoria> categorias = repository.findAll();

        // Assert
        assertAll("findAll",
                () -> assertNotNull(categorias),
                () -> assertFalse(categorias.isEmpty()),
                () -> assertFalse(categorias.isEmpty())
        );
    }

    @Test
    void findAllByNombre() {
        // Act
        List<Categoria> categorias = repository.findAllByNombreContainingIgnoreCase("TEST");

        // Assert
        assertAll("findAllByNombre",
                () -> assertNotNull(categorias),
                () -> assertFalse(categorias.isEmpty()),
                () -> assertEquals("TEST", categorias.get(0).getNombre())
        );
    }

    @Test
    void findByID() {
        // Act
        Categoria categoriaRes = repository.findById(categoria.getId()).orElse(null);

        // Assert
        assertAll("findById",
                () -> assertNotNull(categoriaRes),
                () -> assertEquals("TEST", categoriaRes.getNombre())
        );
    }

    @Test
    void findByIdNotFound() {
        // Act
        Categoria categoria = repository.findById(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a733a")).orElse(null);

        // Assert
        assertNull(categoria);
    }

    @Test
    void save() {
        // Act
        Categoria categoria = repository.save(new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST2", LocalDateTime.now(), LocalDateTime.now(), false));

        // Assert
        assertAll("save",
                () -> assertNotNull(categoria),
                () -> assertEquals("TEST2", categoria.getNombre())
        );
    }

    @Test
    void update() {
        // Act
        var categoriaRes = repository.findById(categoria.getId()).orElse(null);
        Categoria categoriaActualizada = repository.save(new Categoria(categoriaRes.getId(), "TEST3", LocalDateTime.now(), LocalDateTime.now(), false));

        // Assert
        assertAll("update",
                () -> assertNotNull(categoriaActualizada),
                () -> assertEquals("TEST3", categoriaActualizada.getNombre())
        );
    }

    @Test
    void delete() {
        // Act
        var categoriaRes = repository.findById(categoria.getId()).orElse(null);
        repository.delete(categoriaRes);
        Categoria categoriaBorrada = repository.findById(categoria.getId()).orElse(null);

        // Assert
        assertNull(categoriaBorrada);
    }

}
