package com.jorge.gestorTorneos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jorge.gestorTorneos.model.entity.Ronda;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.enums.EstadoRonda;

@Repository
public interface RondaRepository extends JpaRepository<Ronda, Integer> {

    List<Ronda> findByTorneoOrderByNumeroAsc(Torneo torneo);

    Optional<Ronda> findByTorneoAndNumero(Torneo torneo, Integer numero);

    Optional<Ronda> findTopByTorneoOrderByNumeroDesc(Torneo torneo);

    List<Ronda> findByTorneoAndEstado(Torneo torneo, EstadoRonda estado);

    long countByTorneo(Torneo torneo);

    long countByTorneoAndEstado(Torneo torneo, EstadoRonda estado);
}