package com.jorge.gestorTorneos.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

import com.jorge.gestorTorneos.exception.NotFoundException;
import com.jorge.gestorTorneos.model.entity.Clasificacion;
import com.jorge.gestorTorneos.model.entity.Partida;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.EstadoPartida;
import com.jorge.gestorTorneos.repository.ClasificacionRepository;
import com.jorge.gestorTorneos.repository.PartidaRepository;

@Service
public class ClasificacionService {

    private final ClasificacionRepository clasificacionRepository;
    private final PartidaRepository partidaRepository;
    private final TorneoService torneoService;

    public ClasificacionService(ClasificacionRepository clasificacionRepository,
            PartidaRepository partidaRepository,
            TorneoService torneoService) {
        this.clasificacionRepository = clasificacionRepository;
        this.partidaRepository = partidaRepository;
        this.torneoService = torneoService;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Clasificacion> listarPorTorneo(Integer torneoId) {
        return recalcularClasificacion(torneoId);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Clasificacion> recalcularClasificacion(Integer torneoId) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        List<Clasificacion> clasificaciones = clasificacionRepository
                .findByTorneoOrderByPuntosDescDesempateBuchholzDescVictoriasDesc(torneo);

        recalcularBuchholz(torneo, clasificaciones);
        clasificaciones.sort(Comparator
                .comparing((Clasificacion c) -> valorEntero(c.getPuntos())).reversed()
                .thenComparing(c -> valorBigDecimal(c.getDesempateBuchholz()), Comparator.reverseOrder())
                .thenComparing(c -> valorEntero(c.getVictorias()), Comparator.reverseOrder()));

        OffsetDateTime ahora = OffsetDateTime.now();
        for (int i = 0; i < clasificaciones.size(); i++) {
            Clasificacion c = clasificaciones.get(i);
            c.setPosicion(i + 1);
            c.setUltimaActualizacion(ahora);
        }

        return clasificacionRepository.saveAll(clasificaciones);
    }

    private void recalcularBuchholz(Torneo torneo, List<Clasificacion> clasificaciones) {
        Map<Integer, Clasificacion> porUsuarioId = new HashMap<>();
        for (Clasificacion c : clasificaciones) {
            Usuario usuario = c.getUsuario();
            if (usuario != null) {
                porUsuarioId.put(usuario.getId(), c);
            }
        }

        List<Partida> partidasValidadas = partidaRepository
                .findByTorneoAndEstadoAndUsuario2IsNotNullOrderByFechaPartidaAsc(torneo, EstadoPartida.VALIDADA);

        Map<Integer, BigDecimal> sumaBuchholzPorUsuarioId = new HashMap<>();
        for (Partida p : partidasValidadas) {
            Integer id1 = p.getUsuario1() != null ? p.getUsuario1().getId() : null;
            Integer id2 = p.getUsuario2() != null ? p.getUsuario2().getId() : null;
            if (id1 == null || id2 == null) {
                continue;
            }

            Clasificacion c1 = porUsuarioId.get(id1);
            Clasificacion c2 = porUsuarioId.get(id2);
            int puntos1 = c1 != null ? valorEntero(c1.getPuntos()) : 0;
            int puntos2 = c2 != null ? valorEntero(c2.getPuntos()) : 0;

            sumaBuchholzPorUsuarioId.merge(id1, BigDecimal.valueOf(puntos2), BigDecimal::add);
            sumaBuchholzPorUsuarioId.merge(id2, BigDecimal.valueOf(puntos1), BigDecimal::add);
        }

        for (Clasificacion c : clasificaciones) {
            Integer usuarioId = c.getUsuario() != null ? c.getUsuario().getId() : null;
            BigDecimal buchholz = usuarioId != null
                    ? sumaBuchholzPorUsuarioId.getOrDefault(usuarioId, BigDecimal.ZERO)
                    : BigDecimal.ZERO;
            c.setDesempateBuchholz(buchholz);
        }
    }

    private static int valorEntero(Integer valor) {
        return valor != null ? valor : 0;
    }

    private static BigDecimal valorBigDecimal(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    @Transactional(rollbackFor = Exception.class)
    public Clasificacion aplicarBye(Clasificacion clasificacion) {
        clasificacion.setPuntos(clasificacion.getPuntos() + 3);
        clasificacion.setVictorias(clasificacion.getVictorias() + 1);
        clasificacion.setPartidasJugadas(clasificacion.getPartidasJugadas() + 1);
        clasificacion.setByeAsignado(Boolean.TRUE);
        clasificacion.setUltimaActualizacion(OffsetDateTime.now());
        return clasificacionRepository.save(clasificacion);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void recalcularPorPartidaValidada(Partida partida) {
        Torneo torneo = partida.getTorneo();

        Clasificacion c1 = clasificacionRepository.findByTorneoAndUsuario(torneo, partida.getUsuario1())
                .orElseThrow(() -> new NotFoundException("Clasificación no encontrada para usuario1"));

        Clasificacion c2 = null;
        if (partida.getUsuario2() != null) {
            c2 = clasificacionRepository.findByTorneoAndUsuario(torneo, partida.getUsuario2())
                    .orElseThrow(() -> new NotFoundException("Clasificación no encontrada para usuario2"));
        }

        c1.setPartidasJugadas(c1.getPartidasJugadas() + 1);
        if (c2 != null) {
            c2.setPartidasJugadas(c2.getPartidasJugadas() + 1);
        }

        if (partida.getGanador() != null) {
            Integer ganadorId = partida.getGanador().getId();

            if (ganadorId.equals(partida.getUsuario1().getId())) {
                c1.setVictorias(c1.getVictorias() + 1);
                c1.setPuntos(c1.getPuntos() + 3);

                if (c2 != null) {
                    c2.setDerrotas(c2.getDerrotas() + 1);
                }
            } else if (c2 != null && ganadorId.equals(partida.getUsuario2().getId())) {
                c2.setVictorias(c2.getVictorias() + 1);
                c2.setPuntos(c2.getPuntos() + 3);

                c1.setDerrotas(c1.getDerrotas() + 1);
            }
        } else {
            c1.setEmpates(c1.getEmpates() + 1);
            c1.setPuntos(c1.getPuntos() + 1);

            if (c2 != null) {
                c2.setEmpates(c2.getEmpates() + 1);
                c2.setPuntos(c2.getPuntos() + 1);
            }
        }

        c1.setUltimaActualizacion(OffsetDateTime.now());
        clasificacionRepository.save(c1);

        if (c2 != null) {
            c2.setUltimaActualizacion(OffsetDateTime.now());
            clasificacionRepository.save(c2);
        }

        recalcularClasificacion(torneo.getId());
    }
}