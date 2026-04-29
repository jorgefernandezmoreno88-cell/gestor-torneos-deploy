package com.jorge.gestorTorneos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer>, JpaSpecificationExecutor<Torneo> {

    List<Torneo> findByCreador(Usuario creador);

    List<Torneo> findAllByOrderByFechaCreacionDesc();
}