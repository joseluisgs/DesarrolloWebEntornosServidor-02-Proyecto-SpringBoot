package dev.joseluisgs.tiendaapidaw.rest.productos.mappers;

import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoCreateRequest;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoUpdateRequest;
import dev.joseluisgs.tiendaapidaw.rest.productos.models.Producto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductoMapperTest {

    private final Categoria categoria = new Categoria(UUID.fromString("b3d4931d-c1c0-468b-a4b6-9814017a7339"), "DEPORTES", LocalDateTime.now(), LocalDateTime.now(), false);

    // Inyectamos el mapper
    private final ProductoMapper productoMapper = new ProductoMapper();

    @Test
    void toProduct() {
        // Arrange
        ProductoCreateRequest productoCreateRequest = ProductoCreateRequest.builder()
                .marca("Samsung")
                .modelo("S20")
                .descripcion("Móvil")
                .precio(1000.0)
                .imagen("https://www.samsung.com/es_es/smartphones/galaxy-s20/")
                .categoria(categoria.getNombre())
                .stock(10)
                .build();

        // Act
        var res = productoMapper.toProduct(productoCreateRequest, categoria);

        // Assert
        assertAll(
                () -> assertEquals(productoCreateRequest.getMarca(), res.getMarca()),
                () -> assertEquals(productoCreateRequest.getModelo(), res.getModelo()),
                () -> assertEquals(productoCreateRequest.getDescripcion(), res.getDescripcion()),
                () -> assertEquals(productoCreateRequest.getPrecio(), res.getPrecio()),
                () -> assertEquals(productoCreateRequest.getImagen(), res.getImagen()),
                () -> assertEquals(productoCreateRequest.getCategoria(), res.getCategoria().getNombre()),
                () -> assertEquals(productoCreateRequest.getStock(), res.getStock())
        );
    }

    @Test
    void testToProduct() {
        // Arrange
        Long id = 1L;
        ProductoUpdateRequest productoUpdateRequest = ProductoUpdateRequest.builder()
                .marca("Samsung")
                .modelo("S20")
                .descripcion("Móvil")
                .precio(1000.0)
                .imagen("https://www.samsung.com/es_es/smartphones/galaxy-s20/")
                .categoria(categoria.getNombre())
                .stock(10)
                .isDeleted(false)
                .build();

        Producto producto = Producto.builder()
                .id(id)
                .marca(productoUpdateRequest.getMarca())
                .modelo(productoUpdateRequest.getModelo())
                .descripcion(productoUpdateRequest.getDescripcion())
                .precio(productoUpdateRequest.getPrecio())
                .imagen(productoUpdateRequest.getImagen())
                .stock(productoUpdateRequest.getStock())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .isDeleted(false)
                .categoria(categoria)
                .build();

        // Act
        var res = productoMapper.toProduct(productoUpdateRequest, producto, categoria);

        // Assert
        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(productoUpdateRequest.getMarca(), res.getMarca()),
                () -> assertEquals(productoUpdateRequest.getModelo(), res.getModelo()),
                () -> assertEquals(productoUpdateRequest.getDescripcion(), res.getDescripcion()),
                () -> assertEquals(productoUpdateRequest.getPrecio(), res.getPrecio()),
                () -> assertEquals(productoUpdateRequest.getImagen(), res.getImagen()),
                () -> assertEquals(productoUpdateRequest.getCategoria(), res.getCategoria().getNombre()),
                () -> assertEquals(productoUpdateRequest.getStock(), res.getStock())
        );
    }

    @Test
    void toProductResponseDto() {
        // Arrange
        Producto producto = Producto.builder()
                .id(1L)
                .marca("Samsung")
                .modelo("S20")
                .descripcion("Móvil")
                .precio(1000.0)
                .imagen("https://www.samsung.com/es_es/smartphones/galaxy-s20/")
                .stock(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(UUID.randomUUID())
                .isDeleted(false)
                .categoria(categoria)
                .build();

        // Act
        var res = productoMapper.toProductResponse(producto);

        // Assert
        assertAll(
                () -> assertEquals(producto.getId(), res.getId()),
                () -> assertEquals(producto.getMarca(), res.getMarca()),
                () -> assertEquals(producto.getModelo(), res.getModelo()),
                () -> assertEquals(producto.getDescripcion(), res.getDescripcion()),
                () -> assertEquals(producto.getPrecio(), res.getPrecio()),
                () -> assertEquals(producto.getImagen(), res.getImagen()),
                () -> assertEquals(producto.getCategoria().getNombre(), res.getCategoria()),
                () -> assertEquals(producto.getStock(), res.getStock())
        );
    }
}