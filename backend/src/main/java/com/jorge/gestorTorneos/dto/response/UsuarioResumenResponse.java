package com.jorge.gestorTorneos.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResumenResponse {

    private Integer usuarioId;
    private String nombre;
    private String email;
    private long totalTorneosInscrito;
    private int totalPartidasJugadas;
    private int totalVictorias;
    private int totalDerrotas;
    private int totalEmpates;
    private int totalPuntos;
    private long torneosActivos;
    private long torneosFinalizados;
}
