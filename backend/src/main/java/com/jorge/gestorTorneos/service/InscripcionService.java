package com.jorge.gestorTorneos.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jorge.gestorTorneos.exception.BadRequestException;
import com.jorge.gestorTorneos.exception.ConflictException;
import com.jorge.gestorTorneos.exception.NotFoundException;
import com.jorge.gestorTorneos.model.entity.Clasificacion;
import com.jorge.gestorTorneos.model.entity.Inscripcion;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.EstadoInscripcion;
import com.jorge.gestorTorneos.util.AuthUtil;
import com.jorge.gestorTorneos.model.enums.EstadoTorneo;
import com.jorge.gestorTorneos.repository.ClasificacionRepository;
import com.jorge.gestorTorneos.repository.InscripcionRepository;
import com.jorge.gestorTorneos.repository.UsuarioRepository;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final ClasificacionRepository clasificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final TorneoService torneoService;
    private final AuthUtil authUtil;

    public InscripcionService(
            InscripcionRepository inscripcionRepository,
            ClasificacionRepository clasificacionRepository,
            UsuarioRepository usuarioRepository,
            TorneoService torneoService,
            AuthUtil authUtil) {
        this.inscripcionRepository = inscripcionRepository;
        this.clasificacionRepository = clasificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.torneoService = torneoService;
        this.authUtil = authUtil;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Inscripcion inscribirUsuario(Integer torneoId, Integer usuarioId, Usuario solicitante) {
        if (usuarioId == null) {
            throw new BadRequestException("El id de usuario a inscribir es obligatorio");
        }
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        authUtil.verificarPermisoInscripcion(solicitante, torneo, usuarioId);

        if (torneo.getEstado() != EstadoTorneo.ABIERTO) {
            throw new BadRequestException("Las inscripciones no están abiertas para este torneo");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + usuarioId));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BadRequestException("El usuario está inactivo y no puede inscribirse");
        }

        if (inscripcionRepository.findByTorneoAndUsuario(torneo, usuario).isPresent()) {
            throw new ConflictException("El usuario ya está inscrito en este torneo");
        }

        long inscritosActivos = inscripcionRepository.countByTorneoAndEstado(torneo, EstadoInscripcion.INSCRITO);

        Integer plazasMaximas = torneo.getPlazasMaximas();
        if (plazasMaximas != null && inscritosActivos >= plazasMaximas) {
            throw new ConflictException("No quedan plazas disponibles");
        }

        OffsetDateTime ahora = OffsetDateTime.now();

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setTorneo(torneo);
        inscripcion.setUsuario(usuario);
        inscripcion.setEstado(EstadoInscripcion.INSCRITO);
        inscripcion.setFechaInscripcion(ahora);

        Inscripcion guardada = inscripcionRepository.save(inscripcion);

        if (clasificacionRepository.findByTorneoAndUsuario(torneo, usuario).isEmpty()) {
            Clasificacion clasificacion = new Clasificacion();
            clasificacion.setTorneo(torneo);
            clasificacion.setUsuario(usuario);
            clasificacion.setPuntos(0);
            clasificacion.setPosicion(0);
            clasificacion.setVictorias(0);
            clasificacion.setDerrotas(0);
            clasificacion.setEmpates(0);
            clasificacion.setPartidasJugadas(0);
            clasificacion.setDesempateBuchholz(BigDecimal.ZERO);
            clasificacion.setByeAsignado(Boolean.FALSE);
            clasificacion.setUltimaActualizacion(ahora);

            clasificacionRepository.save(clasificacion);
        }

        return guardada;
    }
    
    public List<Inscripcion> listarPorTorneo(Integer torneoId) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        return inscripcionRepository.findByTorneo(torneo);
    }

    @Transactional(rollbackFor = Exception.class)
    public Inscripcion marcarRetiro(Integer inscripcionId) {
        return retirarInscripcion(inscripcionId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Inscripcion retirarInscripcion(Integer inscripcionId) {
        return cambiarEstadoInscripcion(inscripcionId, EstadoInscripcion.RETIRADO);
    }

    @Transactional(rollbackFor = Exception.class)
    public Inscripcion cancelarInscripcion(Integer inscripcionId) {
        return cambiarEstadoInscripcion(inscripcionId, EstadoInscripcion.CANCELADO);
    }

    private Inscripcion cambiarEstadoInscripcion(Integer inscripcionId, EstadoInscripcion nuevoEstado) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new NotFoundException("Inscripción no encontrada con id: " + inscripcionId));

        EstadoInscripcion actual = inscripcion.getEstado();

        if (actual == EstadoInscripcion.RETIRADO || actual == EstadoInscripcion.CANCELADO) {
            if (actual == nuevoEstado) {
                if (actual == EstadoInscripcion.RETIRADO) {
                    throw new ConflictException("La inscripción ya estaba retirada");
                }
                throw new ConflictException("La inscripción ya estaba cancelada");
            }
            if (actual == EstadoInscripcion.RETIRADO) {
                throw new ConflictException("No se puede cancelar una inscripción retirada");
            }
            throw new ConflictException("No se puede retirar una inscripción cancelada");
        }

        if (actual == EstadoInscripcion.ELIMINADO) {
            throw new ConflictException("La inscripción no admite este cambio de estado");
        }

        inscripcion.setEstado(nuevoEstado);
        return inscripcionRepository.save(inscripcion);
    }
}