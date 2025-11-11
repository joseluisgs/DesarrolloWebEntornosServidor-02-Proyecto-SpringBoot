package dev.joseluisgs.tiendaapidaw.rest.users.dto;

import dev.joseluisgs.tiendaapidaw.rest.users.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "Nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "Apellidos no puede estar vacío")
    private String apellidos;

    @NotBlank(message = "Username no puede estar vacío")
    private String username;

    @Email(regexp = ".*@.*\\..*", message = "Email debe ser válido")
    @NotBlank(message = "Email no puede estar vacío")
    private String email;

    @NotBlank(message = "Password no puede estar vacío")
    @Length(min = 5, message = "Password debe tener al menos 5 caracteres")
    @Size(min = 5, message = "Password debe tener al menos 5 caracteres")
    private String password;

    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);

    @Builder.Default
    private Boolean isDeleted = false;
}
