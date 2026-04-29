package com.jorge.gestorTorneos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jorge.gestorTorneos.model.entity.Clasificacion;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;

@Repository
public interface ClasificacionRepository extends JpaRepository<Clasificacion, Integer> {

    List<Clasificacion> findByTorneoOrderByPuntosDescDesempateBuchholzDescVictoriasDesc(Torneo torneo);

    Optional<Clasificacion> findByTorneoAndUsuario(Torneo torneo, Usuario usuario);

    List<Clasificacion> findByUsuario(Usuario usuario);

    @Query("SELECT c FROM Clasificacion c JOIN FETCH c.usuario")
    List<Clasificacion> findAllWithUsuario();
}