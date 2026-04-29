package com.jorge.gestorTorneos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jorge.gestorTorneos.model.entity.Inscripcion;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.EstadoInscripcion;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {

    @EntityGraph(attributePaths = { "usuario", "torneo" })
    List<Inscripcion> findByTorneo(Torneo torneo);

    List<Inscripcion> findByTorneoAndEstado(Torneo torneo, EstadoInscripcion estado);

    @EntityGraph(attributePaths = { "usuario" })
    List<Inscripcion> findByTorneoAndEstadoOrderByFechaInscripcionAsc(Torneo torneo, EstadoInscripcion estado);

    Optional<Inscripcion> findByTorneoAndUsuario(Torneo torneo, Usuario usuario);

    @EntityGraph(attributePaths = { "torneo" })
    List<Inscripcion> findByUsuarioAndEstado(Usuario usuario, EstadoInscripcion estado);

    long countByUsuarioAndEstado(Usuario usuario, EstadoInscripcion estado);

    long countByTorneo(Torneo torneo);

    long countByTorneoAndEstado(Torneo torneo, EstadoInscripcion estado);
}