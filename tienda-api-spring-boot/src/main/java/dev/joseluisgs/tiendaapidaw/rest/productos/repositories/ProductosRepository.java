package dev.joseluisgs.tiendaapidaw.rest.productos.repositories;

import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapidaw.rest.productos.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Creamos el repositorio extendéndolo de JPA, siguiendo DAO
 * Con ello ya tenemos las operaciones básicas de CRUD y Paginación
 * extiende de JpaSpecificationExecutor para tener las opciones de Specificación y busqueda con Criteria
 */
@Repository
public interface ProductosRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    // Por UUID
    Optional<Producto> findByUuid(UUID uuid);

    // Si está borrado
    List<Producto> findByIsDeleted(Boolean isDeleted);

    // Actualizar el producto con isDeleted a true
    @Modifying // Para indicar que es una consulta de actualización
    @Query("UPDATE Producto p SET p.isDeleted = true WHERE p.id = :id")
    // Consulta de actualización
    void updateIsDeletedToTrueById(Long id);

    // Buscamos si existe una categoria con el mismo id
    boolean existsByCategoriaId(UUID id);

    // Añade este método para buscar productos por categoría
    List<Producto> findByCategoria(Categoria categoria);

    List<Producto> findByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}
