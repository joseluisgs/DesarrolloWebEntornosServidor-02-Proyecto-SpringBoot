package dev.joseluisgs.tiendaapispringboot.rest.productos.services;

import dev.joseluisgs.tiendaapispringboot.rest.productos.dto.ProductoCreateRequest;
import dev.joseluisgs.tiendaapispringboot.rest.productos.dto.ProductoResponse;
import dev.joseluisgs.tiendaapispringboot.rest.productos.dto.ProductoUpdateRequest;
import dev.joseluisgs.tiendaapispringboot.rest.productos.models.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductosService {
    Page<ProductoResponse> findAll(Optional<String> marca, Optional<String> categoria, Optional<String> modelo, Optional<Boolean> isDeleted, Optional<Double> precioMax, Optional<Double> stockMin, Pageable pageable);

    ProductoResponse findById(Long id);

    ProductoResponse findbyUuid(String uuid);

    ProductoResponse save(ProductoCreateRequest productoCreateRequest);

    ProductoResponse update(Long id, ProductoUpdateRequest productoUpdateRequest);

    void deleteById(Long id);

    ProductoResponse updateImage(Long id, MultipartFile image, Boolean withUrl);

    List<Producto> findByCreatedAtBetween(LocalDateTime ultimaEjecucion, LocalDateTime ahora);
}
