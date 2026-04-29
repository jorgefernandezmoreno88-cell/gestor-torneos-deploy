package com.jorge.gestorTorneos.dto.response;

import java.time.OffsetDateTime;

import com.jorge.gestorTorneos.model.entity.Partida;
import com.jorge.gestorTorneos.model.enums.EstadoPartida;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartidaResponse {

    private Integer id;
    private String resultado;
    private OffsetDateTime fechaPartida;
    private EstadoPartida estado;
    private Integer torneoId;
    private Integer rondaId;
    private Integer rondaNumero;
    private Integer usuario1Id;
    private String usuario1Nombre;
    private Integer usuario2Id;
    private String usuario2Nombre;
    private Integer ganadorId;
    private String ganadorNombre;

    public static PartidaResponse fromEntity(Partida partida) {
        return PartidaResponse.builder()
                .id(partida.getId())
                .resultado(partida.getResultado())
                .fechaPartida(partida.getFechaPartida())
                .estado(partida.getEstado())
                .torneoId(partida.getTorneo() != null ? partida.getTorneo().getId() : null)
                .rondaId(partida.getRonda() != null ? partida.getRonda().getId() : null)
                .rondaNumero(partida.getRonda() !=null ? partida.getRonda().getNumero(): null)
                .usuario1Id(partida.getUsuario1() != null ? partida.getUsuario1().getId() : null)
                .usuario1Nombre(partida.getUsuario1() != null ? partida.getUsuario1().getNombre() : null)
                .usuario2Id(partida.getUsuario2() != null ? partida.getUsuario2().getId() : null)
                .usuario2Nombre(partida.getUsuario2() != null ? partida.getUsuario2().getNombre() : null)
                .ganadorId(partida.getGanador() != null ? partida.getGanador().getId() : null)
                .ganadorNombre(partida.getGanador() != null ? partida.getGanador().getNombre() : null)
                .build();
    }
}
