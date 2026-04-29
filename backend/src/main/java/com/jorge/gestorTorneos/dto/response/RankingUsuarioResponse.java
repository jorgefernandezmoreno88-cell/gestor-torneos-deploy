package com.jorge.gestorTorneos.dto.response;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankingUsuarioResponse {

    private Integer usuarioId;
    private String nombre;
    private String email;
    private int totalPuntos;
    private int totalVictorias;
    private int totalDerrotas;
    private int totalEmpates;
    private int totalPartidasJugadas;
    private int totalTorneos;
    private BigDecimal promedioPuntos;
    private int posicionGlobal;

    public static BigDecimal calcularPromedio(int totalPuntos, int totalTorneos) {
        if (totalTorneos <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(totalPuntos)
                .divide(BigDecimal.valueOf(totalTorneos), 2, RoundingMode.HALF_UP);
    }
}
