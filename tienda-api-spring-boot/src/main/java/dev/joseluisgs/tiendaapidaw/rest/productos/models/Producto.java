package dev.joseluisgs.tiendaapidaw.rest.productos.models;

import dev.joseluisgs.tiendaapidaw.rest.categorias.models.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true) // JPA Necesita un constructor vacío
@Builder
@Entity // Para que sea una entidad de JPA
@Table(name = "PRODUCTOS") // Para indicar la tabla de la BD, si no coge el nombre de la clase
@EntityListeners(AuditingEntityListener.class) // Para que sea auditada y se autorellene
@Schema(name = "Productos") // Para indicar el nombre de la tabla en la
public class Producto {
    public static final String IMAGE_DEFAULT = "https://via.placeholder.com/150";

    @Id // Indicamos que es el ID de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador del producto", example = "1")
    // Indicamos que es autoincremental y sobre todo por el script de datos
    private Long id;
    @NotBlank(message = "El nombre no puede estar vacío")
    @Schema(description = "Marca del producto", example = "Babolat")
    private String marca;
    @NotBlank(message = "El modelo no puede estar vacío")
    @Schema(description = "Modelo del producto", example = "Aero 98")
    private String modelo;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Length(min = 3, message = "La descripción debe tener al menos 3 caracteres")
    @Schema(description = "Descripción del producto", example = "Raqueta de tenis")
    private String descripcion;
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(columnDefinition = "double precision default 0.0")
    @Schema(description = "Precio del producto", example = "100.0")
    @Builder.Default
    private Double precio = 0.0;
    @Column(columnDefinition = "TEXT default '" + IMAGE_DEFAULT + "'") // Por defecto una imagen
    @Schema(description = "Imagen del producto", example = "https://via.placeholder.com/150")
    @Builder.Default
    private String imagen = IMAGE_DEFAULT;
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(columnDefinition = "integer default 0")
    @Schema(description = "Stock del producto", example = "10")
    @Builder.Default
    private Integer stock = 0;
    @Column(unique = true, updatable = false, nullable = false)
    @Schema(description = "UUID del producto", example = "123e4567-e89b-12d3-a456-426614174000")
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
    @CreationTimestamp
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Schema(description = "Fecha de creación del producto", example = "2021-01-01T00:00:00.000Z")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @UpdateTimestamp
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Schema(description = "Fecha de actualización del producto", example = "2021-01-01T00:00:00.000Z")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(columnDefinition = "boolean default false")
    @Schema(description = "Si el producto está eliminado", example = "false")
    @Builder.Default
    private Boolean isDeleted = false;


    // Relación con categoría, muchos productos pueden tener una categoría
    @ManyToOne
    @JoinColumn(name = "categoria_id") // Así la vamos a llamar en la BB.DD
    @Schema(description = "Categoría del producto", example = "DEPORTES")
    private Categoria categoria;

}
