package dev.joseluisgs.tiendaapidaw.rest.categorias.mappers;

import dev.joseluisgs.tiendaapidaw.rest.categorias.dto.CategoriaRequest;
import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoriasMapper {
    public Categoria toCategoria(CategoriaRequest request) {
        return new Categoria(
                null,
                request.getNombre(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false
        );
    }

    public Categoria toCategoria(CategoriaRequest request, Categoria categoria) {
        return new Categoria(
                categoria.getId(),
                request.getNombre() != null ? request.getNombre() : categoria.getNombre(),
                categoria.getCreatedAt(),
                LocalDateTime.now(),
                request.getIsDeleted() != null ? request.getIsDeleted() : categoria.getIsDeleted()
        );
    }
}
