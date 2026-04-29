package com.jorge.gestorTorneos.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankingGlobalResponse {

    private Integer posicion;
    private Integer usuarioId;
    private String nombre;
    private String email;
    private Integer puntos;
    private Integer victorias;
    private Integer empates;
    private Integer derrotas;
    private Integer partidasJugadas;
    private Integer torneosJugados;
}
