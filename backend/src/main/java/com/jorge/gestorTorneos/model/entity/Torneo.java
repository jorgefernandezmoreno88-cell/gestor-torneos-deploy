package com.jorge.gestorTorneos.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.jorge.gestorTorneos.model.enums.EstadoTorneo;
import com.jorge.gestorTorneos.model.enums.FormatoTorneo;
import com.jorge.gestorTorneos.model.enums.NivelTorneo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * La columna {@code embedding vector(1536)} existe en PostgreSQL (ver migración Flyway)
 * para búsqueda semántica futura; no está mapeada aquí para no acoplar tipos JDBC de pgvector.
 */
@Entity
@Table(name = "torneos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false)
    private OffsetDateTime  fechaInicio;

    @Column(name = "fecha_fin")
    private OffsetDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EstadoTorneo estado;

    @Column(name = "num_rondas", nullable = false)
    private Integer numRondas;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private FormatoTorneo formato;

    @Column(name = "plazas_maximas", nullable = false)
    private Integer plazasMaximas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private NivelTorneo nivel;

    @Column(name = "coste_inscripcion", nullable = false, precision = 10, scale = 2)
    private BigDecimal costeInscripcion;
}
