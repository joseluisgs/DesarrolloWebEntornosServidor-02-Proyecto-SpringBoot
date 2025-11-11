package dev.joseluisgs.tiendaapidaw.rest.productos.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@Schema(description = "Producto a crear")
public class ProductoCreateRequest {
    private static final String IMAGE_DEFAULT = "https://via.placeholder.com/150";

    @NotBlank(message = "El nombre no puede estar vacío")
    @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    @Schema(description = "Marca del producto", example = "Babolat")
    private final String marca;

    @NotBlank(message = "El modelo no puede estar vacío")
    @Schema(description = "Modelo del producto", example = "Aero 98")
    private final String modelo;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Length(min = 3, message = "La descripción debe tener al menos 3 caracteres")
    @Schema(description = "Descripción del producto", example = "Raqueta de tenis")
    private final String descripcion;

    @Min(value = 0, message = "El precio no puede ser negativo")
    @Schema(description = "Precio del producto", example = "100.0")
    private final Double precio;

    @Schema(description = "Imagen del producto", example = "https://via.placeholder.com/150")
    @Builder.Default
    private final String imagen = IMAGE_DEFAULT;

    @NotBlank(message = "La categoría no puede estar vacía")
    @Schema(description = "Categoría del producto", example = "DEPORTES")
    private final String categoria;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Stock del producto", example = "10")
    private final Integer stock;
}
