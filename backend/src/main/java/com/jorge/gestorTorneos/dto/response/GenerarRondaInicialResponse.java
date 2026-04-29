package com.jorge.gestorTorneos.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.jorge.gestorTorneos.model.entity.Partida;
import com.jorge.gestorTorneos.model.entity.Ronda;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerarRondaInicialResponse {

    private RondaResponse ronda;
    private List<PartidaResponse> partidas;

    public static GenerarRondaInicialResponse of(Ronda ronda, List<Partida> partidas) {
        return GenerarRondaInicialResponse.builder()
                .ronda(RondaResponse.fromEntity(ronda))
                .partidas(partidas.stream().map(PartidaResponse::fromEntity).collect(Collectors.toList()))
                .build();
    }
}
