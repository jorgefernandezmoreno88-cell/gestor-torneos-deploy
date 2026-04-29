package com.jorge.gestorTorneos.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clasificacion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Clasificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    @Column(nullable = false)
    private Integer puntos;

    private Integer posicion;

    @Column(name = "ultima_actualizacion", nullable = false)
    private OffsetDateTime ultimaActualizacion;

    @Column(nullable = false)
    private Integer victorias;

    @Column(nullable = false)
    private Integer derrotas;

    @Column(nullable = false)
    private Integer empates;

    @Column(name = "partidas_jugadas", nullable = false)
    private Integer partidasJugadas;

    @Column(name = "desempate_buchholz", nullable = false, precision = 12, scale = 4)
    private BigDecimal desempateBuchholz;

    @Column(name = "bye_asignado", nullable = false)
    private Boolean byeAsignado;

    @PreUpdate
    void marcarUltimaActualizacion() {
        this.ultimaActualizacion = OffsetDateTime.now();
    }
}