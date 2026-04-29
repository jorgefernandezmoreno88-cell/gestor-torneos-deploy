package com.jorge.gestorTorneos.dto.request;

import com.jorge.gestorTorneos.model.enums.RolUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearUsuarioRequest {

    @NotBlank(message = "no debe estar vacío")
    private String nombre;

    @NotBlank(message = "no debe estar vacío")
    @Email(message = "debe ser un email válido")
    private String email;

    @NotBlank(message = "no debe estar vacío")
    private String password;

    private RolUsuario rol;
}
