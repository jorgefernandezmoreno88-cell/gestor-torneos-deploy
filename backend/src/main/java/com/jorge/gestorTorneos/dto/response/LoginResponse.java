package com.jorge.gestorTorneos.dto.response;

import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.RolUsuario;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private Integer id;
    private String nombre;
    private String email;
    private RolUsuario rol;
    private Boolean activo;
    private String token;

    public static LoginResponse fromEntity(Usuario usuario, String token) {
        return LoginResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .activo(usuario.getActivo())
                .token(token)
                .build();
    }
}
