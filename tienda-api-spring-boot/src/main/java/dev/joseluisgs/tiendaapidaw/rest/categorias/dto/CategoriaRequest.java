package dev.joseluisgs.tiendaapidaw.rest.categorias.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CategoriaRequest {
    @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private final String nombre;

    private final Boolean isDeleted;
}
