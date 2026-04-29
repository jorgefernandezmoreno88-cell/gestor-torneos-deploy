package com.jorge.gestorTorneos.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jorge.gestorTorneos.exception.BadRequestException;
import com.jorge.gestorTorneos.exception.ConflictException;
import com.jorge.gestorTorneos.exception.NotFoundException;
import com.jorge.gestorTorneos.dto.request.ActualizarTorneoRequest;
import com.jorge.gestorTorneos.dto.response.TorneoResumenResponse;
import com.jorge.gestorTorneos.dto.request.CrearTorneoRequest;
import com.jorge.gestorTorneos.dto.request.CambiarEstadoTorneoRequest;
import com.jorge.gestorTorneos.model.entity.Clasificacion;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.repository.ClasificacionRepository;
import com.jorge.gestorTorneos.repository.InscripcionRepository;
import com.jorge.gestorTorneos.repository.PartidaRepository;
import com.jorge.gestorTorneos.repository.RondaRepository;
import com.jorge.gestorTorneos.repository.TorneoRepository;
import com.jorge.gestorTorneos.repository.TorneoSpecifications;
import com.jorge.gestorTorneos.model.enums.EstadoInscripcion;
import com.jorge.gestorTorneos.model.enums.EstadoPartida;
import com.jorge.gestorTorneos.model.enums.EstadoRonda;
import com.jorge.gestorTorneos.model.enums.EstadoTorneo;
import com.jorge.gestorTorneos.model.enums.FormatoTorneo;
import com.jorge.gestorTorneos.model.enums.NivelTorneo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TorneoService {

    private final TorneoRepository torneoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final RondaRepository rondaRepository;
    private final PartidaRepository partidaRepository;
    private final ClasificacionRepository clasificacionRepository;

   

    public List<Torneo> listarTodos() {
        return torneoRepository.findAllByOrderByFechaCreacionDesc();
    }

    @Transactional(readOnly = true)
    public Page<Torneo> listarConFiltros(EstadoTorneo estado, NivelTorneo nivel, FormatoTorneo formato,
                                         int page, int size) {
        Specification<Torneo> spec = TorneoSpecifications.conFiltros(estado, nivel, formato);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        return torneoRepository.findAll(spec, pageable);
    }

    public Torneo obtenerPorId(Integer id) {
        return torneoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Torneo no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public TorneoResumenResponse obtenerResumen(Integer torneoId) {
        Torneo torneo = obtenerPorId(torneoId);

        long totalInscritos = inscripcionRepository.countByTorneoAndEstado(torneo, EstadoInscripcion.INSCRITO);
        Integer plazasMax = torneo.getPlazasMaximas();
        Integer plazasDisponibles = plazasMax != null ? plazasMax - (int) totalInscritos : null;

        long totalRondas = rondaRepository.countByTorneo(torneo);
        long rondasFinalizadas = rondaRepository.countByTorneoAndEstado(torneo, EstadoRonda.FINALIZADA);
        long totalPartidas = partidaRepository.countByTorneo(torneo);
        long partidasPendientes = partidaRepository.countByTorneoAndEstado(torneo, EstadoPartida.PENDIENTE);
        long partidasValidadas = partidaRepository.countByTorneoAndEstado(torneo, EstadoPartida.VALIDADA);
        BigDecimal porcentajeOcupacion = calcularPorcentaje(totalInscritos, plazasMax != null ? plazasMax.longValue() : 0L);
        BigDecimal porcentajePartidasValidadas = calcularPorcentaje(partidasValidadas, totalPartidas);

        List<Clasificacion> ranking = clasificacionRepository
                .findByTorneoOrderByPuntosDescDesempateBuchholzDescVictoriasDesc(torneo);
        long totalVictorias = ranking.stream().mapToLong(c -> valorEntero(c.getVictorias())).sum();
        long totalEmpates = ranking.stream().mapToLong(c -> valorEntero(c.getEmpates())).sum();
        long totalDerrotas = ranking.stream().mapToLong(c -> valorEntero(c.getDerrotas())).sum();
        long jugadoresConBye = ranking.stream().filter(c -> Boolean.TRUE.equals(c.getByeAsignado())).count();
        long totalJugadoresClasificados = ranking.size();
        long sumaPuntos = ranking.stream().mapToLong(c -> valorEntero(c.getPuntos())).sum();
        BigDecimal mediaPuntosPorJugador = totalJugadoresClasificados > 0
                ? BigDecimal.valueOf(sumaPuntos)
                        .divide(BigDecimal.valueOf(totalJugadoresClasificados), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Integer liderId = null;
        String liderNombre = null;
        Integer liderPuntos = null;
        if (!ranking.isEmpty()) {
            Clasificacion top = ranking.get(0);
            liderPuntos = top.getPuntos();
            if (top.getUsuario() != null) {
                liderId = top.getUsuario().getId();
                liderNombre = top.getUsuario().getNombre();
            }
        }

        return TorneoResumenResponse.builder()
                .torneoId(torneo.getId())
                .nombreTorneo(torneo.getNombre())
                .estado(torneo.getEstado())
                .formato(torneo.getFormato())
                .nivel(torneo.getNivel())
                .plazasMaximas(plazasMax)
                .totalInscritos(totalInscritos)
                .plazasDisponibles(plazasDisponibles)
                .totalRondas(totalRondas)
                .rondasFinalizadas(rondasFinalizadas)
                .totalPartidas(totalPartidas)
                .partidasPendientes(partidasPendientes)
                .partidasValidadas(partidasValidadas)
                .porcentajeOcupacion(porcentajeOcupacion)
                .porcentajePartidasValidadas(porcentajePartidasValidadas)
                .mediaPuntosPorJugador(mediaPuntosPorJugador)
                .totalVictorias(totalVictorias)
                .totalEmpates(totalEmpates)
                .totalDerrotas(totalDerrotas)
                .jugadoresConBye(jugadoresConBye)
                .torneoFinalizado(torneo.getEstado() == EstadoTorneo.FINALIZADO)
                .liderId(liderId)
                .liderNombre(liderNombre)
                .liderPuntos(liderPuntos)
                .build();
    }

    private static BigDecimal calcularPorcentaje(long numerador, long denominador) {
        if (denominador <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerador)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominador), 2, RoundingMode.HALF_UP);
    }

    private static int valorEntero(Integer valor) {
        return valor != null ? valor : 0;
    }

    public Torneo crearTorneo(CrearTorneoRequest request, Usuario creador) {
        if (creador == null || creador.getId() == null) {
            throw new BadRequestException("Usuario autenticado no válido");
        }

        if (!Boolean.TRUE.equals(creador.getActivo())) {
            throw new BadRequestException("El usuario creador está desactivado");
        }

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del torneo es obligatorio");
        }

        if (request.getFechaInicio() == null) {
            throw new BadRequestException("La fecha de inicio es obligatoria");
        }

        if (request.getFechaFin() == null) {
            throw new BadRequestException("La fecha de fin es obligatoria");
        }

        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        if (request.getFormato() == null) {
            throw new BadRequestException("El formato del torneo es obligatorio");
        }

        if (request.getNivel() == null) {
            throw new BadRequestException("El nivel del torneo es obligatorio");
        }

        if (request.getPlazasMaximas() == null || request.getPlazasMaximas() <= 0) {
            throw new BadRequestException("Las plazas máximas deben ser mayores que 0");
        }

        if (request.getNumRondas() == null || request.getNumRondas() <= 0) {
            throw new BadRequestException("El número de rondas debe ser mayor que 0");
        }

        if (request.getCosteInscripcion() == null) {
            throw new BadRequestException("El coste de inscripción es obligatorio");
        }

        if (request.getCosteInscripcion().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("El coste de inscripción no puede ser negativo");
        }

        Torneo torneo = new Torneo();
        torneo.setNombre(request.getNombre().trim());
        torneo.setDescripcion(request.getDescripcion());
        torneo.setFechaInicio(request.getFechaInicio());
        torneo.setFechaFin(request.getFechaFin());
        torneo.setFormato(request.getFormato());
        torneo.setNivel(request.getNivel());
        torneo.setPlazasMaximas(request.getPlazasMaximas());
        torneo.setNumRondas(request.getNumRondas());
        torneo.setCosteInscripcion(request.getCosteInscripcion());
        torneo.setCreador(creador);

        torneo.setEstado(EstadoTorneo.PENDIENTE);
        torneo.setFechaCreacion(OffsetDateTime.now());

        return torneoRepository.save(torneo);
    }

    public Torneo actualizarTorneo(Integer id, ActualizarTorneoRequest request) {
        validarActualizarTorneoRequest(request);

        Torneo torneo = obtenerPorId(id);

        torneo.setNombre(request.getNombre().trim());
        torneo.setDescripcion(request.getDescripcion());
        torneo.setFechaInicio(request.getFechaInicio());
        torneo.setFechaFin(request.getFechaFin());
        torneo.setFormato(request.getFormato());
        torneo.setNivel(request.getNivel());
        torneo.setPlazasMaximas(request.getPlazasMaximas());
        torneo.setNumRondas(request.getNumRondas());
        torneo.setCosteInscripcion(request.getCosteInscripcion());

        return torneoRepository.save(torneo);
    }

    private void validarActualizarTorneoRequest(ActualizarTorneoRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del torneo es obligatorio");
        }

        if (request.getFechaInicio() == null) {
            throw new BadRequestException("La fecha de inicio es obligatoria");
        }

        if (request.getFechaFin() == null) {
            throw new BadRequestException("La fecha de fin es obligatoria");
        }

        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        if (request.getFormato() == null) {
            throw new BadRequestException("El formato del torneo es obligatorio");
        }

        if (request.getNivel() == null) {
            throw new BadRequestException("El nivel del torneo es obligatorio");
        }

        if (request.getPlazasMaximas() == null || request.getPlazasMaximas() <= 0) {
            throw new BadRequestException("Las plazas máximas deben ser mayores que 0");
        }

        if (request.getNumRondas() == null || request.getNumRondas() <= 0) {
            throw new BadRequestException("El número de rondas debe ser mayor que 0");
        }

        if (request.getCosteInscripcion() == null) {
            throw new BadRequestException("El coste de inscripción es obligatorio");
        }

        if (request.getCosteInscripcion().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("El coste de inscripción no puede ser negativo");
        }
    }
    
    public Torneo cambiarEstado(Integer torneoId, CambiarEstadoTorneoRequest request) {
        if (request.getEstado() == null) {
            throw new BadRequestException("Debe indicarse un nuevo estado");
        }
        return cambiarEstado(torneoId, request.getEstado());
    }

    public Torneo abrirTorneo(Integer torneoId) {
        return cambiarEstado(torneoId, EstadoTorneo.ABIERTO);
    }

    public Torneo cerrarTorneo(Integer torneoId) {
        return cambiarEstado(torneoId, EstadoTorneo.CERRADO);
    }

    public Torneo iniciarTorneo(Integer torneoId) {
        return cambiarEstado(torneoId, EstadoTorneo.EN_CURSO);
    }

    public Torneo finalizarTorneo(Integer torneoId) {
        return cambiarEstado(torneoId, EstadoTorneo.FINALIZADO);
    }

    public Torneo cancelarTorneo(Integer torneoId) {
        return cambiarEstado(torneoId, EstadoTorneo.CANCELADO);
    }

    private Torneo cambiarEstado(Integer torneoId, EstadoTorneo nuevo) {
        Torneo torneo = obtenerPorId(torneoId);
        EstadoTorneo actual = torneo.getEstado();
        validarCambioEstado(torneo, actual, nuevo);
        torneo.setEstado(nuevo);
        return torneoRepository.save(torneo);
    }
    
    private void validarCambioEstado(Torneo torneo, EstadoTorneo actual, EstadoTorneo nuevo) {
        if (actual == EstadoTorneo.CANCELADO || actual == EstadoTorneo.FINALIZADO) {
            throw new ConflictException("No se puede cambiar el estado de un torneo cancelado o finalizado");
        }

        if (actual == nuevo) {
            throw new ConflictException("El torneo ya está en ese estado");
        }

        switch (nuevo) {
            case ABIERTO -> {
                if (torneo.getNombre() == null || torneo.getFormato() == null || torneo.getPlazasMaximas() == null) {
                    throw new BadRequestException("No se puede abrir el torneo: faltan datos obligatorios");
                }
            }
            case EN_CURSO -> {
                long inscritos = inscripcionRepository.findByTorneoAndEstado(
                        torneo, EstadoInscripcion.INSCRITO).size();

                if (inscritos < 2) {
                    throw new BadRequestException("No se puede iniciar el torneo con menos de 2 inscritos");
                }

                if (actual != EstadoTorneo.CERRADO && actual != EstadoTorneo.ABIERTO) {
                    throw new BadRequestException("El torneo solo puede pasar a EN_CURSO desde ABIERTO o CERRADO");
                }
            }
            case FINALIZADO -> {
                // luego puedes endurecer esto aún más
                if (actual != EstadoTorneo.EN_CURSO) {
                    throw new BadRequestException("Solo se puede finalizar un torneo que esté en curso");
                }
            }
            default -> {
            }
        }
    }
}