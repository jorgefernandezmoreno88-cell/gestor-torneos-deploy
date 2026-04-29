package com.jorge.gestorTorneos.dto.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.jorge.gestorTorneos.model.enums.FormatoTorneo;
import com.jorge.gestorTorneos.model.enums.NivelTorneo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ActualizarTorneoRequest {

    @NotBlank(message = "no debe estar vacío")
    private String nombre;

    private String descripcion;

    @NotNull(message = "es obligatorio")
    private OffsetDateTime fechaInicio;

    @NotNull(message = "es obligatorio")
    private OffsetDateTime fechaFin;

    @NotNull(message = "es obligatorio")
    private FormatoTorneo formato;

    @NotNull(message = "es obligatorio")
    private NivelTorneo nivel;

    @NotNull(message = "es obligatorio")
    @Positive(message = "debe ser mayor que 0")
    private Integer plazasMaximas;

    @NotNull(message = "es obligatorio")
    @Positive(message = "debe ser mayor que 0")
    private Integer numRondas;

    @NotNull(message = "es obligatorio")
    @PositiveOrZero(message = "no debe ser negativo")
    private BigDecimal costeInscripcion;
}
