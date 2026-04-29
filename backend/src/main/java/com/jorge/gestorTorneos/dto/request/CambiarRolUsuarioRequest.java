package com.jorge.gestorTorneos.dto.request;

import com.jorge.gestorTorneos.model.enums.RolUsuario;

import lombok.Data;

@Data
public class CambiarRolUsuarioRequest {
    private RolUsuario rol;
}
