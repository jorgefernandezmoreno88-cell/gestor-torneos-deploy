package com.jorge.gestorTorneos.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnfrentamientoExisteResponse {

    private Integer torneoId;
    private Integer usuario1Id;
    private Integer usuario2Id;
    private boolean existe;
    private int totalEnfrentamientos;
}
