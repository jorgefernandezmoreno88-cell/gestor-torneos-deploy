package com.jorge.gestorTorneos.service;

import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jorge.gestorTorneos.exception.BadRequestException;
import com.jorge.gestorTorneos.exception.ConflictException;
import com.jorge.gestorTorneos.exception.NotFoundException;
import com.jorge.gestorTorneos.dto.request.CrearRondaRequest;
import com.jorge.gestorTorneos.dto.response.GenerarRondaInicialResponse;
import com.jorge.gestorTorneos.model.entity.Clasificacion;
import com.jorge.gestorTorneos.model.entity.Inscripcion;
import com.jorge.gestorTorneos.model.entity.Partida;
import com.jorge.gestorTorneos.model.entity.Ronda;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.EstadoInscripcion;
import com.jorge.gestorTorneos.model.enums.EstadoPartida;
import com.jorge.gestorTorneos.model.enums.EstadoRonda;
import com.jorge.gestorTorneos.model.enums.EstadoTorneo;
import com.jorge.gestorTorneos.repository.InscripcionRepository;
import com.jorge.gestorTorneos.repository.PartidaRepository;
import com.jorge.gestorTorneos.repository.RondaRepository;
import com.jorge.gestorTorneos.repository.TorneoRepository;

@Service
public class RondaService {

    private final RondaRepository rondaRepository;
    private final PartidaRepository partidaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ClasificacionService clasificacionService;
    private final TorneoService torneoService;
    private final TorneoRepository torneoRepository;

    public RondaService(RondaRepository rondaRepository,
                        PartidaRepository partidaRepository,
                        InscripcionRepository inscripcionRepository,
                        ClasificacionService clasificacionService,
                        TorneoService torneoService,
                        TorneoRepository torneoRepository) {
        this.rondaRepository = rondaRepository;
        this.partidaRepository = partidaRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.clasificacionService = clasificacionService;
        this.torneoService = torneoService;
        this.torneoRepository = torneoRepository;
    }

    public List<Ronda> listarRondasPorTorneo(Integer torneoId) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        return rondaRepository.findByTorneoOrderByNumeroAsc(torneo);
    }

    public List<Ronda> listarPorTorneo(Integer torneoId) {
        return listarRondasPorTorneo(torneoId);
    }

    public Ronda obtenerRondaPorId(Integer rondaId) {
        return rondaRepository.findById(rondaId)
                .orElseThrow(() -> new NotFoundException("Ronda no encontrada con id: " + rondaId));
    }

    public List<Partida> listarPartidasPorRonda(Integer rondaId) {
        Ronda ronda = obtenerRondaPorId(rondaId);
        return partidaRepository.findByRondaOrderByFechaPartidaAsc(ronda);
    }

    @Transactional(rollbackFor = Exception.class)
    public GenerarRondaInicialResponse generarRondaInicial(Integer torneoId) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);

        EstadoTorneo estadoTorneo = torneo.getEstado();
        if (estadoTorneo != EstadoTorneo.CERRADO && estadoTorneo != EstadoTorneo.EN_CURSO) {
            throw new BadRequestException(
                    "Solo se puede generar la ronda inicial si el torneo está CERRADO o EN_CURSO");
        }

        List<Inscripcion> inscripciones = inscripcionRepository
                .findByTorneoAndEstadoOrderByFechaInscripcionAsc(torneo, EstadoInscripcion.INSCRITO);

        if (inscripciones.size() < 2) {
            throw new BadRequestException("Se necesitan al menos 2 jugadores inscritos para generar la ronda inicial");
        }

        if (rondaRepository.findByTorneoAndNumero(torneo, 1).isPresent()) {
            throw new ConflictException("Ya existe la ronda número 1 para este torneo");
        }

        List<Clasificacion> clasificacionOrdenada = clasificacionService.listarPorTorneo(torneoId);
        Clasificacion byeClasificacion = asignarByeSiNecesario(clasificacionOrdenada);

        if (byeClasificacion != null) {
            Integer byeUsuarioId = byeClasificacion.getUsuario().getId();
            inscripciones.removeIf(i -> i.getUsuario() == null || byeUsuarioId.equals(i.getUsuario().getId()));
        }

        OffsetDateTime ahora = OffsetDateTime.now();

        Ronda ronda = new Ronda();
        
        ronda.setTorneo(torneo);
        ronda.setNumero(1);
        
        ronda.setEstado(EstadoRonda.PENDIENTE);
        ronda.setFechaCreacion(ahora);
        Ronda rondaGuardada = rondaRepository.save(ronda);
        
        torneo.setEstado(EstadoTorneo.EN_CURSO);
        torneoRepository.save(torneo);

        List<Partida> partidasAGuardar = new ArrayList<>();
        for (int i = 0; i + 1 < inscripciones.size(); i += 2) {
            Partida partida = new Partida();
            partida.setTorneo(torneo);
            partida.setRonda(rondaGuardada);
            partida.setUsuario1(inscripciones.get(i).getUsuario());
            partida.setUsuario2(inscripciones.get(i + 1).getUsuario());
            partida.setEstado(EstadoPartida.PENDIENTE);
            partida.setFechaPartida(ahora);
            partida.setResultado(null);
            partida.setGanador(null);
            partidasAGuardar.add(partida);
        }

        List<Partida> partidasGuardadas = partidaRepository.saveAll(partidasAGuardar);
        clasificacionService.recalcularClasificacion(torneoId);
        return GenerarRondaInicialResponse.of(rondaGuardada, partidasGuardadas);
    }

    @Transactional(rollbackFor = Exception.class)
    public GenerarRondaInicialResponse generarSiguienteRonda(Integer torneoId) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);

        if (torneo.getEstado() != EstadoTorneo.EN_CURSO) {
            throw new BadRequestException("Solo se puede generar la siguiente ronda si el torneo está EN_CURSO");
        }

        Ronda ultimaRonda = rondaRepository.findTopByTorneoOrderByNumeroDesc(torneo)
                .orElseThrow(() -> new BadRequestException("Debe existir al menos una ronda previa"));

        if (ultimaRonda.getEstado() != EstadoRonda.FINALIZADA) {
            throw new BadRequestException("La última ronda debe estar finalizada");
        }

        List<Partida> partidasUltima = partidaRepository.findByRondaOrderByFechaPartidaAsc(ultimaRonda);
        if (partidasUltima.isEmpty()) {
            throw new BadRequestException("La última ronda no tiene partidas registradas");
        }
        for (Partida p : partidasUltima) {
            if (p.getEstado() != EstadoPartida.VALIDADA) {
                throw new BadRequestException("Todas las partidas de la última ronda deben estar validadas");
            }
        }

        Integer numRondasMax = torneo.getNumRondas();
        if (numRondasMax != null && ultimaRonda.getNumero() >= numRondasMax) {
            throw new ConflictException("El torneo ya ha alcanzado el número máximo de rondas");
        }

        int siguienteNumero = ultimaRonda.getNumero() + 1;
        if (rondaRepository.findByTorneoAndNumero(torneo, siguienteNumero).isPresent()) {
            throw new ConflictException("Ya existe la ronda número " + siguienteNumero + " para este torneo");
        }

        List<Clasificacion> clasificacionOrdenada = clasificacionService.listarPorTorneo(torneoId);
        if (clasificacionOrdenada.size() < 2) {
            throw new BadRequestException("Se necesitan al menos 2 jugadores en la clasificación para generar emparejamientos");
        }

        asignarByeSiNecesario(clasificacionOrdenada);

        OffsetDateTime ahora = OffsetDateTime.now();

        Ronda nuevaRonda = new Ronda();
        nuevaRonda.setTorneo(torneo);
        nuevaRonda.setNumero(siguienteNumero);
        nuevaRonda.setEstado(EstadoRonda.PENDIENTE);
        nuevaRonda.setFechaCreacion(ahora);
        Ronda rondaGuardada = rondaRepository.save(nuevaRonda);

        List<AbstractMap.SimpleImmutableEntry<Usuario, Usuario>> pares =
                crearEmparejamientosEvitandoRepetidos(torneo, clasificacionOrdenada);

        List<Partida> partidasAGuardar = new ArrayList<>();
        for (AbstractMap.SimpleImmutableEntry<Usuario, Usuario> par : pares) {
            Partida partida = new Partida();
            partida.setTorneo(torneo);
            partida.setRonda(rondaGuardada);
            partida.setUsuario1(par.getKey());
            partida.setUsuario2(par.getValue());
            partida.setEstado(EstadoPartida.PENDIENTE);
            partida.setFechaPartida(ahora);
            partida.setResultado(null);
            partida.setGanador(null);
            partidasAGuardar.add(partida);
        }

        List<Partida> partidasGuardadas = partidaRepository.saveAll(partidasAGuardar);
        clasificacionService.recalcularClasificacion(torneoId);
        return GenerarRondaInicialResponse.of(rondaGuardada, partidasGuardadas);
    }

    private Clasificacion asignarByeSiNecesario(List<Clasificacion> clasificaciones) {
        if (clasificaciones.size() % 2 == 0) {
            return null;
        }

        int idxBye = -1;
        for (int i = clasificaciones.size() - 1; i >= 0; i--) {
            Clasificacion c = clasificaciones.get(i);
            if (!Boolean.TRUE.equals(c.getByeAsignado())) {
                idxBye = i;
                break;
            }
        }

        if (idxBye < 0) {
            idxBye = clasificaciones.size() - 1;
        }

        Clasificacion bye = clasificaciones.remove(idxBye);
        clasificacionService.aplicarBye(bye);
        return bye;
    }

    /**
     * Empareja por orden de clasificación: cada jugador libre busca rival en orden de índice,
     * priorizando quien no haya enfrentado antes (histórico del torneo); si no hay, el primer libre.
     */
    private List<AbstractMap.SimpleImmutableEntry<Usuario, Usuario>> crearEmparejamientosEvitandoRepetidos(
            Torneo torneo, List<Clasificacion> clasificacionOrdenada) {
        Set<String> enfrentamientosPrevios = clavesEnfrentamientosPrevios(torneo);
        List<Usuario> jugadores = new ArrayList<>(clasificacionOrdenada.size());
        for (Clasificacion c : clasificacionOrdenada) {
            jugadores.add(c.getUsuario());
        }

        int n = jugadores.size();
        boolean[] emparejado = new boolean[n];
        List<AbstractMap.SimpleImmutableEntry<Usuario, Usuario>> pares = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (emparejado[i]) {
                continue;
            }
            Usuario a = jugadores.get(i);
            int rival = indicePrimerRivalDisponible(jugadores, emparejado, i, a, enfrentamientosPrevios, true);
            if (rival < 0) {
                rival = indicePrimerRivalDisponible(jugadores, emparejado, i, a, enfrentamientosPrevios, false);
            }
            if (rival < 0) {
                break;
            }
            int mejorIdx = Math.min(i, rival);
            int peorIdx = Math.max(i, rival);
            emparejado[mejorIdx] = true;
            emparejado[peorIdx] = true;
            pares.add(new AbstractMap.SimpleImmutableEntry<>(jugadores.get(mejorIdx), jugadores.get(peorIdx)));
        }

        return pares;
    }

    private int indicePrimerRivalDisponible(List<Usuario> jugadores, boolean[] emparejado, int i, Usuario a,
            Set<String> enfrentamientosPrevios, boolean evitarRepetidos) {
        for (int j = 0; j < jugadores.size(); j++) {
            if (j == i || emparejado[j]) {
                continue;
            }
            Usuario b = jugadores.get(j);
            if (evitarRepetidos && yaSeEnfrentaron(enfrentamientosPrevios, a, b)) {
                continue;
            }
            return j;
        }
        return -1;
    }

    private Set<String> clavesEnfrentamientosPrevios(Torneo torneo) {
        Set<String> claves = new HashSet<>();
        for (Partida p : partidaRepository.findByTorneoOrderByFechaPartidaAsc(torneo)) {
            if (p.getUsuario2() == null) {
                continue;
            }
            claves.add(claveParIds(p.getUsuario1().getId(), p.getUsuario2().getId()));
        }
        return claves;
    }

    private static String claveParIds(Integer id1, Integer id2) {
        int x = Math.min(id1, id2);
        int y = Math.max(id1, id2);
        return x + ":" + y;
    }

    private static boolean yaSeEnfrentaron(Set<String> claves, Usuario u1, Usuario u2) {
        return claves.contains(claveParIds(u1.getId(), u2.getId()));
    }

    public Ronda crearRonda(Integer torneoId, CrearRondaRequest request) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        
        if (torneo.getEstado() != EstadoTorneo.EN_CURSO) {
            throw new BadRequestException("Solo se pueden crear rondas en torneos EN_CURSO");
        }

        if (request.getNumero() == null || request.getNumero() < 1) {
            throw new BadRequestException("El número de ronda debe ser válido");
        }

        List<Ronda> rondas = rondaRepository.findByTorneoOrderByNumeroAsc(torneo);

        boolean numeroRepetido = rondas.stream()
                .anyMatch(r -> r.getNumero().equals(request.getNumero()));

        if (numeroRepetido) {
            throw new ConflictException("Ya existe una ronda con ese número");
        }

        boolean hayRondaEnCurso = rondas.stream()
                .anyMatch(r -> r.getEstado() == EstadoRonda.EN_CURSO);

        if (hayRondaEnCurso) {
            throw new ConflictException("No se puede crear una nueva ronda mientras haya otra en curso");
        }

        if (torneo.getNumRondas() != null && request.getNumero() > torneo.getNumRondas()) {
            throw new BadRequestException("El número de ronda supera el máximo configurado para el torneo");
        }

        Ronda ronda = new Ronda();
        ronda.setTorneo(torneo);
        ronda.setNumero(request.getNumero());
        ronda.setEstado(EstadoRonda.EN_CURSO);
        ronda.setFechaCreacion(OffsetDateTime.now());

        return rondaRepository.save(ronda);
    }

    public Ronda iniciarRonda(Integer rondaId) {
        Ronda ronda = obtenerRondaPorId(rondaId);
        if (ronda.getEstado() != EstadoRonda.PENDIENTE) {
            throw new BadRequestException("Solo se puede iniciar una ronda que esté pendiente");
        }
        ronda.setEstado(EstadoRonda.EN_CURSO);
        return rondaRepository.save(ronda);
    }

    public Ronda finalizarRonda(Integer rondaId) {
        Ronda ronda = obtenerRondaPorId(rondaId);
        if (ronda.getEstado() != EstadoRonda.EN_CURSO) {
            throw new BadRequestException("Solo se puede finalizar una ronda que esté en curso");
        }

        List<Partida> partidas = partidaRepository.findByRondaOrderByFechaPartidaAsc(ronda);
        for (Partida p : partidas) {
            if (p.getEstado() != EstadoPartida.VALIDADA) {
                throw new ConflictException("No se puede finalizar la ronda porque hay partidas pendientes");
            }
        }

        ronda.setEstado(EstadoRonda.FINALIZADA);
        Ronda rondaGuardada = rondaRepository.save(ronda);

        Torneo torneoDeRonda = rondaGuardada.getTorneo();
        Integer numRondasMax = torneoDeRonda.getNumRondas();
        if (numRondasMax != null && rondaGuardada.getNumero() >= numRondasMax) {
            torneoDeRonda.setEstado(EstadoTorneo.FINALIZADO);
            torneoRepository.save(torneoDeRonda);
        }

        return rondaGuardada;
    }

    public Ronda finalizarRonda(Integer torneoId, Integer rondaId) {
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        Ronda ronda = obtenerRondaPorId(rondaId);
        if (!ronda.getTorneo().getId().equals(torneo.getId())) {
            throw new BadRequestException("La ronda no pertenece al torneo indicado");
        }
        return finalizarRonda(rondaId);
    }
}