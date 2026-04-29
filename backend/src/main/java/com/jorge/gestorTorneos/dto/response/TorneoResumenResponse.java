package com.jorge.gestorTorneos.dto.response;

import java.math.BigDecimal;

import com.jorge.gestorTorneos.model.enums.EstadoTorneo;
import com.jorge.gestorTorneos.model.enums.FormatoTorneo;
import com.jorge.gestorTorneos.model.enums.NivelTorneo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TorneoResumenResponse {

    private Integer torneoId;
    private String nombreTorneo;
    private EstadoTorneo estado;
    private FormatoTorneo formato;
    private NivelTorneo nivel;
    private Integer plazasMaximas;
    private Long totalInscritos;
    private Integer plazasDisponibles;
    private Long totalRondas;
    private Long rondasFinalizadas;
    private Long totalPartidas;
    private Long partidasPendientes;
    private Long partidasValidadas;
    private BigDecimal porcentajeOcupacion;
    private BigDecimal porcentajePartidasValidadas;
    private BigDecimal mediaPuntosPorJugador;
    private Long totalVictorias;
    private Long totalEmpates;
    private Long totalDerrotas;
    private Long jugadoresConBye;
    private Boolean torneoFinalizado;
    private Integer liderId;
    private String liderNombre;
    private Integer liderPuntos;
}
