package com.jorge.gestorTorneos.service;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jorge.gestorTorneos.dto.request.CrearUsuarioRequest;
import com.jorge.gestorTorneos.dto.response.UsuarioResumenResponse;
import com.jorge.gestorTorneos.exception.BadRequestException;
import com.jorge.gestorTorneos.exception.ConflictException;
import com.jorge.gestorTorneos.exception.NotFoundException;
import com.jorge.gestorTorneos.model.entity.Clasificacion;
import com.jorge.gestorTorneos.model.entity.Inscripcion;
import com.jorge.gestorTorneos.model.entity.Partida;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.EstadoInscripcion;
import com.jorge.gestorTorneos.model.enums.EstadoTorneo;
import com.jorge.gestorTorneos.model.enums.RolUsuario;
import com.jorge.gestorTorneos.repository.ClasificacionRepository;
import com.jorge.gestorTorneos.repository.InscripcionRepository;
import com.jorge.gestorTorneos.repository.PartidaRepository;
import com.jorge.gestorTorneos.repository.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final InscripcionRepository inscripcionRepository;
	private final PartidaRepository partidaRepository;
	private final ClasificacionRepository clasificacionRepository;

	public UsuarioService(UsuarioRepository usuarioRepository,
	                      PasswordEncoder passwordEncoder,
	                      InscripcionRepository inscripcionRepository,
	                      PartidaRepository partidaRepository,
	                      ClasificacionRepository clasificacionRepository) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.inscripcionRepository = inscripcionRepository;
		this.partidaRepository = partidaRepository;
		this.clasificacionRepository = clasificacionRepository;
	}

	public Usuario crearUsuario(CrearUsuarioRequest request) {

		if (request.getNombre() == null || request.getNombre().isBlank()) {
			throw new BadRequestException("El nombre es obligatorio");
		}

		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new BadRequestException("El email es obligatorio");
		}

		if (request.getPassword() == null || request.getPassword().isBlank()) {
			throw new BadRequestException("La contraseña es obligatoria");
		}

		if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new ConflictException("El email ya está registrado");
		}

		Usuario usuario = new Usuario();
		usuario.setNombre(request.getNombre());
		usuario.setEmail(request.getEmail());
		usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		usuario.setRol(RolUsuario.USUARIO);
		usuario.setActivo(true);
		usuario.setFechaRegistro(OffsetDateTime.now());

		return usuarioRepository.save(usuario);
	}

    public Usuario cambiarRol(Integer id, RolUsuario rol) {
        if (rol == null) {
            throw new BadRequestException("El rol es obligatorio");
        }
        Usuario usuario = obtenerPorId(id);
        usuario.setRol(rol);
        return usuarioRepository.save(usuario);
    }

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}
	
	public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con id: " + id));
    }

	@Transactional(readOnly = true)
	public List<Torneo> listarTorneosInscrito(Integer usuarioId) {
		Usuario usuario = obtenerPorId(usuarioId);
		return inscripcionRepository.findByUsuarioAndEstado(usuario, EstadoInscripcion.INSCRITO).stream()
				.map(Inscripcion::getTorneo)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<Partida> listarPartidas(Integer usuarioId) {
		Usuario usuario = obtenerPorId(usuarioId);
		return partidaRepository.findByUsuarioParticipante(usuario);
	}

	@Transactional(readOnly = true)
	public List<Clasificacion> listarClasificaciones(Integer usuarioId) {
		Usuario usuario = obtenerPorId(usuarioId);
		return clasificacionRepository.findByUsuario(usuario);
	}

	@Transactional(readOnly = true)
	public UsuarioResumenResponse obtenerResumen(Integer usuarioId) {
		Usuario usuario = obtenerPorId(usuarioId);
		long totalTorneosInscrito = inscripcionRepository.countByUsuarioAndEstado(usuario, EstadoInscripcion.INSCRITO);
		List<Clasificacion> clasificaciones = clasificacionRepository.findByUsuario(usuario);

		int totalPartidasJugadas = 0;
		int totalVictorias = 0;
		int totalDerrotas = 0;
		int totalEmpates = 0;
		int totalPuntos = 0;
		Set<Integer> torneosActivosIds = new HashSet<>();
		Set<Integer> torneosFinalizadosIds = new HashSet<>();

		for (Clasificacion c : clasificaciones) {
			totalPartidasJugadas += valorEntero(c.getPartidasJugadas());
			totalVictorias += valorEntero(c.getVictorias());
			totalDerrotas += valorEntero(c.getDerrotas());
			totalEmpates += valorEntero(c.getEmpates());
			totalPuntos += valorEntero(c.getPuntos());

			Torneo torneo = c.getTorneo();
			if (torneo == null || torneo.getId() == null || torneo.getEstado() == null) {
				continue;
			}
			if (torneo.getEstado() == EstadoTorneo.FINALIZADO) {
				torneosFinalizadosIds.add(torneo.getId());
			} else if (torneo.getEstado() != EstadoTorneo.CANCELADO) {
				torneosActivosIds.add(torneo.getId());
			}
		}

		return UsuarioResumenResponse.builder()
				.usuarioId(usuario.getId())
				.nombre(usuario.getNombre())
				.email(usuario.getEmail())
				.totalTorneosInscrito(totalTorneosInscrito)
				.totalPartidasJugadas(totalPartidasJugadas)
				.totalVictorias(totalVictorias)
				.totalDerrotas(totalDerrotas)
				.totalEmpates(totalEmpates)
				.totalPuntos(totalPuntos)
				.torneosActivos(torneosActivosIds.size())
				.torneosFinalizados(torneosFinalizadosIds.size())
				.build();
	}

	private static int valorEntero(Integer v) {
		return v != null ? v : 0;
	}

    public Usuario desactivar(Integer id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setActivo(false);
        return usuarioRepository.save(usuario);
    }

    public Usuario activar(Integer id) {
        Usuario usuario = obtenerPorId(id);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }
}