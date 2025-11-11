package dev.joseluisgs.tiendaapidaw.rest.categorias.controllers;

import dev.joseluisgs.tiendaapidaw.rest.categorias.dto.CategoriaRequest;
import dev.joseluisgs.tiendaapidaw.rest.categorias.exceptions.CategoriaConflict;
import dev.joseluisgs.tiendaapidaw.rest.categorias.exceptions.CategoriaNotFound;
import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapidaw.rest.categorias.services.CategoriasService;
import dev.joseluisgs.tiendaapidaw.utils.pagination.PageResponse;
import dev.joseluisgs.tiendaapidaw.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("${api.version}/categorias") // Es la ruta del controlador
@Slf4j
// Categorias capadas solo para admin
@PreAuthorize("hasRole('USER')") // Solo los usuarios pueden acceder
public class CategoriasRestController {
    // Repositorio de productos
    private final CategoriasService categoriasService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public CategoriasRestController(CategoriasService categoriasService, PaginationLinksUtils paginationLinksUtils) {
        this.categoriasService = categoriasService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    /**
     * Obtiene todas las categorias
     *
     * @param nombre    Nombre de la categoría
     * @param isDeleted Si está borrado
     * @return Page de categorías
     */
    @GetMapping()
    public ResponseEntity<PageResponse<Categoria>> getAllCategories(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Buscando todos las categorias con nombre: " + nombre + " y borrados: " + isDeleted);
        // Creamos el objeto de ordenación
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Categoria> pageResult = categoriasService.findAll(nombre, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));

    }

    /**
     * Obtiene una categoría por su id
     *
     * @param id del categoría, se pasa como parámetro de la URL /{id}
     * @return Categoria si existe
     * @throws CategoriaNotFound si no existe la categoría (404)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getCategoryById(@PathVariable UUID id) {
        log.info("Buscando producto por id: " + id);
        return ResponseEntity.ok(categoriasService.findById(id));
    }

    /**
     * Crear una categoria
     *
     * @param categoriaCreateDto a crear
     * @return Categoria creada
     * @throws HttpClientErrorException.BadRequest si la categoría no es correcta (400)
     */
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> createCategory(@Valid @RequestBody CategoriaRequest categoriaCreateDto) {
        log.info("Creando categegoría: " + categoriaCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriasService.save(categoriaCreateDto));
    }

    /**
     * Actualizar una categoría
     *
     * @param id                 de la categoría, se pasa como parámetro de la URL /{id}
     * @param categoriaUpdateDto a actualizar
     * @return Categoria actualizada
     * @throws CategoriaNotFound                   si no existe el producto (404)
     * @throws HttpClientErrorException.BadRequest si el producto no es correcto (400)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoriaRequest categoriaUpdateDto) {
        log.info("Actualizando categoria por id: " + id + " con categoria: " + categoriaUpdateDto);
        return ResponseEntity.ok(categoriasService.update(id, categoriaUpdateDto));
    }


    /**
     * Borrar una categoría
     *
     * @param id del categoria, se pasa como parámetro de la URL /{id}
     * @return No Content si se ha borrado
     * @throws CategoriaNotFound si no existe el producto (404)
     * @throws CategoriaConflict si no se puede borrar porque tiene productos asociados (409)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        log.info("Borrando categoria por id: " + id);
        categoriasService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Manejador de excepciones de Validación: 400 Bad Request
     *
     * @param ex excepción
     * @return Mapa de errores de validación con el campo y el mensaje
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
