package com.jorge.gestorTorneos.dto.response;

import java.time.OffsetDateTime;

import com.jorge.gestorTorneos.model.entity.Inscripcion;
import com.jorge.gestorTorneos.model.enums.EstadoInscripcion;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InscripcionResponse {

    private Integer id;
    private EstadoInscripcion estado;
    private OffsetDateTime fechaInscripcion;
    private Integer usuarioId;
    private String usuarioNombre;
    private Integer torneoId;

    public static InscripcionResponse fromEntity(Inscripcion inscripcion) {
        return InscripcionResponse.builder()
                .id(inscripcion.getId())
                .estado(inscripcion.getEstado())
                .fechaInscripcion(inscripcion.getFechaInscripcion())
                .usuarioId(inscripcion.getUsuario() != null ? inscripcion.getUsuario().getId() : null)
                .usuarioNombre(inscripcion.getUsuario() != null ? inscripcion.getUsuario().getNombre() : null)
                .torneoId(inscripcion.getTorneo() != null ? inscripcion.getTorneo().getId() : null)
                .build();
    }
}
