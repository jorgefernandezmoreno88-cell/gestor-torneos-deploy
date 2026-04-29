package com.jorge.gestorTorneos.dto.response;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.enums.EstadoTorneo;
import com.jorge.gestorTorneos.model.enums.FormatoTorneo;
import com.jorge.gestorTorneos.model.enums.NivelTorneo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TorneoResponse {

    private Integer id;
    private String nombre;
    private String descripcion;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFin;
    private EstadoTorneo estado;
    private FormatoTorneo formato;
    private NivelTorneo nivel;
    private Integer plazasMaximas;
    private Integer numRondas;
    private BigDecimal costeInscripcion;
    private OffsetDateTime fechaCreacion;
    private Integer creadorId;
    private String creadorNombre;

    public static TorneoResponse fromEntity(Torneo torneo) {
        return TorneoResponse.builder()
                .id(torneo.getId())
                .nombre(torneo.getNombre())
                .descripcion(torneo.getDescripcion())
                .fechaInicio(torneo.getFechaInicio())
                .fechaFin(torneo.getFechaFin())
                .estado(torneo.getEstado())
                .formato(torneo.getFormato())
                .nivel(torneo.getNivel())
                .plazasMaximas(torneo.getPlazasMaximas())
                .numRondas(torneo.getNumRondas())
                .costeInscripcion(torneo.getCosteInscripcion())
                .fechaCreacion(torneo.getFechaCreacion())
                .creadorId(torneo.getCreador() != null ? torneo.getCreador().getId() : null)
                .creadorNombre(torneo.getCreador() != null ? torneo.getCreador().getNombre() : null)
                .build();
    }
}
