package dev.joseluisgs.tiendaapidaw.rest.productos.mappers;

import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoCreateRequest;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoResponse;
import dev.joseluisgs.tiendaapidaw.rest.productos.dto.ProductoUpdateRequest;
import dev.joseluisgs.tiendaapidaw.rest.productos.models.Producto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductoMapper {
    public Producto toProduct(ProductoCreateRequest request, Categoria categoria) {
        return Producto.builder()
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .imagen(request.getImagen())
                .stock(request.getStock())
                .categoria(categoria)
                .build();
    }

    public Producto toProduct(ProductoUpdateRequest request, Producto producto, Categoria categoria) {
        // Creamos el producto actualizado con los campos que nos llegan actualizando el updateAt y si son null no se actualizan y se quedan los anteriores
        return Producto.builder()
                .id(producto.getId())
                .marca(request.getMarca() != null ? request.getMarca() : producto.getMarca())
                .modelo(request.getModelo() != null ? request.getModelo() : producto.getModelo())
                .descripcion(request.getDescripcion() != null ? request.getDescripcion() : producto.getDescripcion())
                .precio(request.getPrecio() != null ? request.getPrecio() : producto.getPrecio())
                .imagen(request.getImagen() != null ? request.getImagen() : producto.getImagen())
                .stock(request.getStock() != null ? request.getStock() : producto.getStock())
                .uuid(producto.getUuid())
                .createdAt(producto.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .categoria(categoria)
                .build();
    }

    public ProductoResponse toProductResponse(Producto producto) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .marca(producto.getMarca())
                .modelo(producto.getModelo())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .imagen(producto.getImagen())
                .categoria(producto.getCategoria().getNombre())
                .stock(producto.getStock())
                .createdAt(producto.getCreatedAt())
                .updatedAt(producto.getUpdatedAt())
                .uuid(producto.getUuid())
                .isDeleted(producto.getIsDeleted())
                .build();
    }
}
