package com.jorge.gestorTorneos.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrarResultadoPartidaRequest {

    @NotBlank(message = "no debe estar vacío")
    private String resultado;
    private Integer ganadorId;
    private Boolean empate;
}
