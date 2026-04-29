package com.jorge.gestorTorneos.dto.response;

import java.time.OffsetDateTime;

import com.jorge.gestorTorneos.model.entity.Ronda;
import com.jorge.gestorTorneos.model.enums.EstadoRonda;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RondaResponse {

    private Integer id;
    private Integer numero;
    private EstadoRonda estado;
    private OffsetDateTime fechaCreacion;
    private Integer torneoId;

    public static RondaResponse fromEntity(Ronda ronda) {
        return RondaResponse.builder()
                .id(ronda.getId())
                .numero(ronda.getNumero())
                .estado(ronda.getEstado())
                .fechaCreacion(ronda.getFechaCreacion())
                .torneoId(ronda.getTorneo() != null ? ronda.getTorneo().getId() : null)
                .build();
    }
}
