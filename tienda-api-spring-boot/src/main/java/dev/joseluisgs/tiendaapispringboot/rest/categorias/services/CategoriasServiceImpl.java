package dev.joseluisgs.tiendaapispringboot.rest.categorias.services;

import dev.joseluisgs.tiendaapispringboot.rest.categorias.dto.CategoriaRequest;
import dev.joseluisgs.tiendaapispringboot.rest.categorias.exceptions.CategoriaConflict;
import dev.joseluisgs.tiendaapispringboot.rest.categorias.exceptions.CategoriaNotFound;
import dev.joseluisgs.tiendaapispringboot.rest.categorias.mappers.CategoriasMapper;
import dev.joseluisgs.tiendaapispringboot.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapispringboot.rest.categorias.repositories.CategoriasRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@CacheConfig(cacheNames = {"categorias"})
public class CategoriasServiceImpl implements CategoriasService {
    private final CategoriasRepository categoriasRepository;
    private final CategoriasMapper categoriasMapper;

    @Autowired
    public CategoriasServiceImpl(CategoriasRepository categoriasRepository, CategoriasMapper categoriasMapper) {
        this.categoriasRepository = categoriasRepository;
        this.categoriasMapper = categoriasMapper;
    }

    @Override
    public Page<Categoria> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todos las categorias con nombre: " + nombre + " y borrados: " + isDeleted);
        // Criterio de búsqueda por nombre
        Specification<Categoria> specNombreCategoria = (root, query, criteriaBuilder) ->
                nombre.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por borrado
        Specification<Categoria> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Combinamos las especificaciones
        Specification<Categoria> criterio = Specification.allOf(
                specNombreCategoria,
                specIsDeleted
        );
        return categoriasRepository.findAll(criterio, pageable);
    }

    @Override
    public Categoria findByNombre(String nombre) {
        log.info("Buscando categoría por nombre: " + nombre);
        return categoriasRepository.findByNombreEqualsIgnoreCase(nombre).orElseThrow(() -> new CategoriaNotFound(nombre));
    }


    @Override
    @Cacheable(key = "#id")
    public Categoria findById(UUID id) {
        log.info("Buscando categoría por id: " + id);
        return categoriasRepository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria save(CategoriaRequest categoriaRequest) {
        log.info("Guardando categoría: " + categoriaRequest);
        // No debe existir una con el mismo nombre
        categoriasRepository.findByNombreEqualsIgnoreCase(categoriaRequest.getNombre()).ifPresent(c -> {
            throw new CategoriaConflict("Ya existe una categoría con el nombre " + categoriaRequest.getNombre());
        });
        return categoriasRepository.save(categoriasMapper.toCategoria(categoriaRequest));
    }

    @Override
    @CachePut(key = "#result.id")
    public Categoria update(UUID id, CategoriaRequest categoriaRequest) {
        log.info("Actualizando categoría: " + categoriaRequest);
        Categoria categoriaActual = categoriasRepository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
        // No debe existir una con el mismo nombre, y si existe soy yo mismo
        categoriasRepository.findByNombreEqualsIgnoreCase(categoriaRequest.getNombre()).ifPresent(c -> {
            if (!c.getId().equals(id)) {
                throw new CategoriaConflict("Ya existe una categoría con el nombre " + categoriaRequest.getNombre());
            }
        });
        // Actualizamos los datos
        return categoriasRepository.save(categoriasMapper.toCategoria(categoriaRequest, categoriaActual));
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional // Para que se haga todo o nada y no se quede a medias (por el update)
    public void deleteById(UUID id) {
        log.info("Borrando categoría por id: " + id);
        Categoria categoria = categoriasRepository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
        // O lo marcamos como borrado, para evitar problemas de cascada, no podemos borrar categorías con productos!!!
        // La otra forma es que comprobaramos si hay productos para borrarlos antes
        // categoriasRepository.updateIsDeletedToTrueById(id);
        // Otra forma es que comprobaramos si hay productos para borrarlos antes
        if (categoriasRepository.existsProductoById(id)) {
            log.warn("No se puede borrar la categoría con id: " + id + " porque tiene productos asociados");
            throw new CategoriaConflict("No se puede borrar la categoría con id " + id + " porque tiene productos asociados");
            // Otra forma es hacer el borrado logico
            // categoriasRepository.updateIsDeletedToTrueById(id);
        } else {
            categoriasRepository.deleteById(id);
        }

    }
}
