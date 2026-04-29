package com.jorge.gestorTorneos.dto.request;

import com.jorge.gestorTorneos.model.enums.RolUsuario;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private RolUsuario rol;
}
