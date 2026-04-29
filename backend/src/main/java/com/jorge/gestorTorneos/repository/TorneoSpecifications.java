package com.jorge.gestorTorneos.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.enums.EstadoTorneo;
import com.jorge.gestorTorneos.model.enums.FormatoTorneo;
import com.jorge.gestorTorneos.model.enums.NivelTorneo;

import jakarta.persistence.criteria.Predicate;

public final class TorneoSpecifications {

    private TorneoSpecifications() {
    }

    public static Specification<Torneo> conFiltros(EstadoTorneo estado, NivelTorneo nivel, FormatoTorneo formato) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (estado != null) {
                predicates.add(cb.equal(root.get("estado"), estado));
            }
            if (nivel != null) {
                predicates.add(cb.equal(root.get("nivel"), nivel));
            }
            if (formato != null) {
                predicates.add(cb.equal(root.get("formato"), formato));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
