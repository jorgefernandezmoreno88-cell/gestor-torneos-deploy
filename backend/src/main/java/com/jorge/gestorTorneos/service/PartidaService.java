package com.jorge.gestorTorneos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jorge.gestorTorneos.dto.request.RegistrarResultadoPartidaRequest;
import com.jorge.gestorTorneos.dto.response.EnfrentamientoExisteResponse;
import com.jorge.gestorTorneos.exception.BadRequestException;
import com.jorge.gestorTorneos.exception.ConflictException;
import com.jorge.gestorTorneos.exception.NotFoundException;
import com.jorge.gestorTorneos.model.entity.Partida;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.EstadoPartida;
import com.jorge.gestorTorneos.model.enums.EstadoRonda;
import com.jorge.gestorTorneos.repository.PartidaRepository;
import com.jorge.gestorTorneos.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final TorneoService torneoService;
    private final ClasificacionService clasificacionService;

    public List<Partida> listarPorTorneo(Integer torneoId) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        return partidaRepository.findByTorneoOrderByFechaPartidaAsc(torneo);
    }

    public List<Partida> listarEnfrentamientos(Integer torneoId, Integer usuario1Id, Integer usuario2Id) {
        validarParametrosEnfrentamiento(usuario1Id, usuario2Id);
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        Usuario u1 = usuarioService.obtenerPorId(usuario1Id);
        Usuario u2 = usuarioService.obtenerPorId(usuario2Id);
        return partidaRepository.buscarEnfrentamientos(torneo, u1, u2);
    }

    public EnfrentamientoExisteResponse existeEnfrentamiento(Integer torneoId, Integer usuario1Id, Integer usuario2Id) {
        validarParametrosEnfrentamiento(usuario1Id, usuario2Id);
        List<Partida> partidas = listarEnfrentamientos(torneoId, usuario1Id, usuario2Id);
        return EnfrentamientoExisteResponse.builder()
                .torneoId(torneoId)
                .usuario1Id(usuario1Id)
                .usuario2Id(usuario2Id)
                .existe(!partidas.isEmpty())
                .totalEnfrentamientos(partidas.size())
                .build();
    }

    public Partida obtenerPorId(Integer partidaId) {
        return partidaRepository.findById(partidaId)
                .orElseThrow(() -> new NotFoundException("Partida no encontrada con id: " + partidaId));
    }

    private void validarParametrosEnfrentamiento(Integer usuario1Id, Integer usuario2Id) {
        if (usuario1Id == null || usuario2Id == null) {
            throw new BadRequestException("usuario1Id y usuario2Id son obligatorios");
        }
        if (usuario1Id.equals(usuario2Id)) {
            throw new BadRequestException("Deben indicarse dos usuarios distintos");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Partida registrarResultado(Integer partidaId, RegistrarResultadoPartidaRequest request) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new NotFoundException("Partida no encontrada con id: " + partidaId));

        if (partida.getEstado() == EstadoPartida.VALIDADA) {
            throw new BadRequestException("La partida ya está validada");
        }
        if (partida.getEstado() == EstadoPartida.CANCELADA) {
            throw new BadRequestException("No se puede registrar resultado en una partida cancelada");
        }
        if (partida.getEstado() != EstadoPartida.PENDIENTE) {
            throw new BadRequestException("Solo se puede registrar el resultado cuando la partida está pendiente");
        }
        if (partida.getRonda() == null || partida.getRonda().getEstado() != EstadoRonda.EN_CURSO) {
            throw new BadRequestException("Solo se puede registrar el resultado si la ronda está en curso");
        }

        if (request.getResultado() == null || request.getResultado().isBlank()) {
            throw new BadRequestException("Debe indicarse un resultado");
        }

        boolean empate = Boolean.TRUE.equals(request.getEmpate());

        if (empate) {
            if (request.getGanadorId() != null) {
                throw new BadRequestException("En caso de empate no debe indicarse ganador");
            }
            if (partida.getUsuario2() == null) {
                throw new BadRequestException("No se puede registrar empate sin segundo jugador");
            }
            String resultadoNorm = request.getResultado().trim();
            boolean resultadoEmpatePermitido =
                    resultadoNorm.equalsIgnoreCase("EMPATE")
                    || resultadoNorm.matches("\\d+\\s*-\\s*\\d+")
                    || resultadoNorm.matches("\\d+(\\.\\d+)?\\s*-\\s*\\d+(\\.\\d+)?");

            if (!resultadoEmpatePermitido) {
                throw new BadRequestException("Para empate puedes escribir EMPATE o un resultado tipo 5-5");
            }
            partida.setResultado(resultadoNorm);
            partida.setGanador(null);
        } else {
            if (request.getGanadorId() == null) {
                throw new BadRequestException("Debe indicarse el ganador cuando no hay empate");
            }
            Usuario ganador = usuarioRepository.findById(request.getGanadorId())
                    .orElseThrow(() -> new NotFoundException("Ganador no encontrado con id: " + request.getGanadorId()));

            boolean esUsuario1 = partida.getUsuario1() != null && ganador.getId().equals(partida.getUsuario1().getId());
            boolean esUsuario2 = partida.getUsuario2() != null && ganador.getId().equals(partida.getUsuario2().getId());

            if (!esUsuario1 && !esUsuario2) {
                throw new BadRequestException("El ganador indicado no participa en esta partida");
            }

            partida.setResultado(request.getResultado().trim());
            partida.setGanador(ganador);
        }

        partida.setEstado(EstadoPartida.VALIDADA);
        Partida guardada = partidaRepository.save(partida);

        clasificacionService.recalcularPorPartidaValidada(guardada);

        return guardada;
    }

    public Partida validar(Integer partidaId) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new NotFoundException("Partida no encontrada con id: " + partidaId));

        if (partida.getEstado() == EstadoPartida.PENDIENTE) {
            throw new BadRequestException("No se puede validar una partida sin resultado");
        }

        if (partida.getEstado() == EstadoPartida.VALIDADA) {
            throw new ConflictException("La partida ya está validada");
        }

        partida.setEstado(EstadoPartida.VALIDADA);
        Partida validada = partidaRepository.save(partida);

        clasificacionService.recalcularPorPartidaValidada(validada);

        return validada;
    }
}
