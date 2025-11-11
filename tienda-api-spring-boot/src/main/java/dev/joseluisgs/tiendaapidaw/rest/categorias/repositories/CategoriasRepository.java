package dev.joseluisgs.tiendaapidaw.rest.categorias.repositories;

import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Creamos el repositorio extendéndolo de JPA, siguiendo DAO
 * Con ello ya tenemos las operaciones básicas de CRUD y Paginación
 * extiende de JpaSpecificationExecutor para tener las opciones de Specificación y busqueda con Criteria
 */
@Repository
public interface CategoriasRepository extends JpaRepository<Categoria, UUID>, JpaSpecificationExecutor<Categoria> {
    // Encontrar por nombre exacto
    Optional<Categoria> findByNombreEqualsIgnoreCase(String nombre);

    // Categorias por nombre
    List<Categoria> findAllByNombreContainingIgnoreCase(String nombre);

    // Si están borradas
    List<Categoria> findByIsDeleted(Boolean isDeleted);

    // Actualizar el producto con isDeleted a true
    @Modifying // Para indicar que es una consulta de actualización
    @Query("UPDATE Categoria p SET p.isDeleted = true WHERE p.id = :id")
    // Consulta de actualización
    void updateIsDeletedToTrueById(UUID id);

    // Obtienes si existe un producto con el id de la categoría
    // Debo hacerlo con consulta porque hay un join
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Producto p WHERE p.categoria.id = :id")
    boolean existsProductoById(UUID id);

}