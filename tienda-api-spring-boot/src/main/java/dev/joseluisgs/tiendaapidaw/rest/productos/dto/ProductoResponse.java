package dev.joseluisgs.tiendaapidaw.rest.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Producto a devolver")
public class ProductoResponse {
    @Schema(description = "Identificador del producto", example = "1")
    private Long id;
    @Schema(description = "Marca del producto", example = "Babolat")
    private String marca;
    @Schema(description = "Modelo del producto", example = "Aero 98")
    private String modelo;
    @Schema(description = "Descripción del producto", example = "Raqueta de tenis")
    private String descripcion;
    @Schema(description = "Precio del producto", example = "100.0")
    private Double precio;
    @Schema(description = "Imagen del producto", example = "https://via.placeholder.com/150")
    private String imagen;
    @Schema(description = "Categoría del producto", example = "DEPORTES")
    private String categoria;
    @Schema(description = "Stock del producto", example = "10")
    private Integer stock;
    @Schema(description = "Fecha de creación del producto", example = "2021-01-01T00:00:00.000Z")
    private LocalDateTime createdAt;
    @Schema(description = "Fecha de actualización del producto", example = "2021-01-01T00:00:00.000Z")
    private LocalDateTime updatedAt;
    @Schema(description = "UUID del producto", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID uuid;
    @Schema(description = "Si el producto está borrado", example = "false")
    private Boolean isDeleted;
}
