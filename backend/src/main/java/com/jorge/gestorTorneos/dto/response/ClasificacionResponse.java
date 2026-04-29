package com.jorge.gestorTorneos.dto.response;

import com.jorge.gestorTorneos.model.entity.Clasificacion;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClasificacionResponse {

	private Integer id;
	private Integer puntos;
	private Integer posicion;
	private Integer victorias;
	private Integer derrotas;
	private Integer empates;
	private Integer partidasJugadas;
	private BigDecimal desempateBuchholz;
	private Integer usuarioId;
	private String usuarioNombre;
	private Integer torneoId;

	public static ClasificacionResponse fromEntity(Clasificacion clasificacion) {
		return ClasificacionResponse.builder().id(clasificacion.getId()).puntos(clasificacion.getPuntos())
				.posicion(clasificacion.getPosicion()).victorias(clasificacion.getVictorias())
				.derrotas(clasificacion.getDerrotas()).empates(clasificacion.getEmpates())
				.partidasJugadas(clasificacion.getPartidasJugadas())
				.desempateBuchholz(clasificacion.getDesempateBuchholz())
				.usuarioId(clasificacion.getUsuario() != null ? clasificacion.getUsuario().getId() : null)
				.usuarioNombre(clasificacion.getUsuario() != null ? clasificacion.getUsuario().getNombre() : null)
				.torneoId(clasificacion.getTorneo() != null ? clasificacion.getTorneo().getId() : null).build();
	}
}
