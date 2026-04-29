package com.jorge.gestorTorneos.dto.response;

import java.time.OffsetDateTime;

import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.RolUsuario;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {

    private Integer id;
    private String nombre;
    private String email;
    private OffsetDateTime fechaRegistro;
    private RolUsuario rol;
    private Boolean activo;

    public static UsuarioResponse fromEntity(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .fechaRegistro(usuario.getFechaRegistro())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .build();
    }
}
