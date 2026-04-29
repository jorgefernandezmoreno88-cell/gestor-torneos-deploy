package com.jorge.gestorTorneos.dto.request;

import com.jorge.gestorTorneos.model.enums.EstadoTorneo;

import lombok.Data;

@Data
public class CambiarEstadoTorneoRequest {
    private EstadoTorneo estado;
}
