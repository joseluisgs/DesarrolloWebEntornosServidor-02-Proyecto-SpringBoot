package dev.joseluisgs.tiendaapidaw.rest.categorias.services;

import dev.joseluisgs.tiendaapidaw.rest.categorias.dto.CategoriaRequest;
import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CategoriasService {
    Page<Categoria> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Categoria findByNombre(String nombre);

    Categoria findById(UUID id);

    Categoria save(CategoriaRequest categoriaRequest);

    Categoria update(UUID id, CategoriaRequest categoriaRequest);

    void deleteById(UUID id);
}
