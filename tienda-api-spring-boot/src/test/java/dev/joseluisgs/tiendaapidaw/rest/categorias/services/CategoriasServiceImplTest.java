package dev.joseluisgs.tiendaapidaw.rest.categorias.services;

import dev.joseluisgs.tiendaapidaw.rest.categorias.dto.CategoriaRequest;
import dev.joseluisgs.tiendaapidaw.rest.categorias.exceptions.CategoriaConflict;
import dev.joseluisgs.tiendaapidaw.rest.categorias.mappers.CategoriasMapper;
import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapidaw.rest.categorias.repositories.CategoriasRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriasServiceImplTest {

    private final Categoria categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), false);
    private final CategoriaRequest categoriaRequest = new CategoriaRequest("TEST", false);
    @Mock
    private CategoriasRepository categoriasRepository;

    @Mock
    private CategoriasMapper categoriasMapper;

    @InjectMocks
    private CategoriasServiceImpl categoriasService;


    @Test
    public void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending()); // ejemplo de creación de un objeto Pageable
        Page<Categoria> expectedPage = new PageImpl<>(List.of(categoria)); // ejemplo de creación de un objeto Page
        // Arrange
        when(categoriasRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);


        // Act
        var res = categoriasService.findAll(Optional.empty(), Optional.empty(), pageable);

        // Assert
        assertAll("findAll",
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty())
        );

        // Verify
        verify(categoriasRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testFindByNombre() {
        // Arrange
        when(categoriasRepository.findByNombreEqualsIgnoreCase(any(String.class))).thenReturn(Optional.of(categoria));

        // Act
        var res = categoriasService.findByNombre("Test");

        // Assert
        assertAll("findByNombre",
                () -> assertNotNull(res),
                () -> assertEquals("TEST", res.getNombre())
        );

        // Verify
        verify(categoriasRepository, times(1)).findByNombreEqualsIgnoreCase(any(String.class));
    }


    @Test
    public void testFindById() {
        // Arrange
        when(categoriasRepository.findById(any(UUID.class))).thenReturn(Optional.of(categoria));

        // Act
        var res = categoriasService.findById(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"));

        // Assert
        assertAll("findById",
                () -> assertNotNull(res),
                () -> assertEquals("TEST", res.getNombre())
        );

        // Verify
        verify(categoriasRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    public void testSave() {
        // Arrange
        when(categoriasRepository.findByNombreEqualsIgnoreCase(any(String.class))).thenReturn(Optional.empty());
        when(categoriasMapper.toCategoria(any(CategoriaRequest.class))).thenReturn(categoria);
        when(categoriasRepository.save(any(Categoria.class))).thenReturn(categoria);

        // Act
        categoriasService.save(categoriaRequest);

        // Assert
        assertAll("save",
                () -> assertNotNull(categoria),
                () -> assertEquals("TEST", categoria.getNombre())
        );

        // Verify
        verify(categoriasRepository, times(1)).findByNombreEqualsIgnoreCase(any(String.class));
        verify(categoriasRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    public void testSaveConflict() {
        // Arrange
        when(categoriasRepository.findByNombreEqualsIgnoreCase(any(String.class))).thenReturn(Optional.of(categoria));

        // Act
        var res = assertThrows(CategoriaConflict.class, () -> categoriasService.save(categoriaRequest));

        // Assert
        assertAll("saveConflict",
                () -> assertNotNull(res),
                () -> assertEquals("Ya existe una categoría con el nombre TEST", res.getMessage())
        );

        // Verify
        verify(categoriasRepository, times(1)).findByNombreEqualsIgnoreCase(any(String.class));
        verify(categoriasRepository, times(0)).save(any(Categoria.class));
    }

    @Test
    public void testUpdate() {
        // Arrange
        when(categoriasRepository.findById(any(UUID.class))).thenReturn(Optional.of(categoria));
        when(categoriasRepository.findByNombreEqualsIgnoreCase(any(String.class))).thenReturn(Optional.of(categoria));
        when(categoriasMapper.toCategoria(any(CategoriaRequest.class), any(Categoria.class))).thenReturn(categoria);
        when(categoriasRepository.save(any(Categoria.class))).thenReturn(categoria);

        // Act
        categoriasService.update(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), categoriaRequest);

        // Assert
        assertAll("update",
                () -> assertNotNull(categoria),
                () -> assertEquals("TEST", categoria.getNombre())
        );


        // Verify
        verify(categoriasRepository, times(1)).findById(any(UUID.class));
        verify(categoriasRepository, times(1)).findByNombreEqualsIgnoreCase(any(String.class));
        verify(categoriasRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    public void testUpdateConflict() {
        // Arrange
        when(categoriasRepository.findById(any(UUID.class))).thenReturn(Optional.of(categoria));
        when(categoriasRepository.findByNombreEqualsIgnoreCase(any(String.class))).thenReturn(Optional.of(categoria));

        // Act, el id no debe ser igual, no se puede actualizar, porqe ya existe
        var res = assertThrows(CategoriaConflict.class, () -> categoriasService.update(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7338"), categoriaRequest));

        // Assert
        assertAll("updateConflict",
                () -> assertNotNull(res),
                () -> assertEquals("Ya existe una categoría con el nombre TEST", res.getMessage())
        );

        // Verify
        verify(categoriasRepository, times(1)).findById(any(UUID.class));
        verify(categoriasRepository, times(1)).findByNombreEqualsIgnoreCase(any(String.class));
        verify(categoriasRepository, times(0)).save(any(Categoria.class));
    }

    @Test
    public void testDeleteById() {
        // Arrange
        when(categoriasRepository.findById(any(UUID.class))).thenReturn(Optional.of(categoria));
        when(categoriasRepository.existsProductoById(any(UUID.class))).thenReturn(false);

        // Act
        categoriasService.deleteById(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"));

        // Assert

        // Verify
        verify(categoriasRepository, times(1)).findById(any(UUID.class));
        verify(categoriasRepository, times(1)).existsProductoById(any(UUID.class));
        verify(categoriasRepository, times(1)).deleteById(any(UUID.class));
    }
}