package dev.joseluisgs.tiendaapidaw.rest.categorias.mappers;

import dev.joseluisgs.tiendaapidaw.rest.categorias.dto.CategoriaRequest;
import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoriasMapperTest {
    private final Categoria categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "TEST", LocalDateTime.now(), LocalDateTime.now(), false);

    // Inyectamos el mapper
    private final CategoriasMapper categoriaMapper = new CategoriasMapper();

    private final CategoriaRequest categoriaRequest = new CategoriaRequest("TEST", false);

    @Test
    public void whenToCategoria_thenReturnCategoria() {
        Categoria mappedCategoria = categoriaMapper.toCategoria(categoriaRequest);

        assertAll("whenToCategoria_thenReturnCategoria",
                () -> assertEquals(categoriaRequest.getNombre(), mappedCategoria.getNombre()),
                () -> assertEquals(categoriaRequest.getIsDeleted(), mappedCategoria.getIsDeleted())
        );
    }

    @Test
    public void whenToCategoriaWithExistingCategoria_thenReturnUpdatedCategoria() {

        Categoria updatedCategoria = categoriaMapper.toCategoria(categoriaRequest);

        assertAll("whenToCategoriaWithExistingCategoria_thenReturnUpdatedCategoria",
                () -> assertEquals(categoriaRequest.getNombre(), updatedCategoria.getNombre()),
                () -> assertEquals(categoriaRequest.getIsDeleted(), updatedCategoria.getIsDeleted())
        );
    }
}
