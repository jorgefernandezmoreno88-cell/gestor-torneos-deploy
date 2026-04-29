package com.jorge.gestorTorneos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jorge.gestorTorneos.model.entity.Partida;
import com.jorge.gestorTorneos.model.entity.Ronda;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.EstadoPartida;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Integer> {

    List<Partida> findByTorneoOrderByFechaPartidaAsc(Torneo torneo);

    List<Partida> findByRondaOrderByFechaPartidaAsc(Ronda ronda);

    List<Partida> findByRondaAndEstado(Ronda ronda, EstadoPartida estado);

    List<Partida> findByTorneoAndEstadoAndUsuario2IsNotNullOrderByFechaPartidaAsc(Torneo torneo, EstadoPartida estado);

    long countByTorneo(Torneo torneo);

    long countByTorneoAndEstado(Torneo torneo, EstadoPartida estado);

    @Query("SELECT p FROM Partida p WHERE p.torneo = :torneo AND p.usuario2 IS NOT NULL AND "
            + "((p.usuario1 = :u1 AND p.usuario2 = :u2) OR (p.usuario1 = :u2 AND p.usuario2 = :u1)) "
            + "ORDER BY p.ronda.numero ASC, p.fechaPartida ASC, p.id ASC")
    List<Partida> buscarEnfrentamientos(@Param("torneo") Torneo torneo,
                                        @Param("u1") Usuario u1,
                                        @Param("u2") Usuario u2);

    @Query("SELECT p FROM Partida p WHERE p.usuario1 = :u OR p.usuario2 = :u ORDER BY p.fechaPartida DESC, p.id DESC")
    List<Partida> findByUsuarioParticipante(@Param("u") Usuario usuario);
}