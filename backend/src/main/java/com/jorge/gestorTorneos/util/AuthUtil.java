package com.jorge.gestorTorneos.util;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.jorge.gestorTorneos.exception.BadRequestException;
import com.jorge.gestorTorneos.exception.ForbiddenException;
import com.jorge.gestorTorneos.exception.NotFoundException;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.RolUsuario;
import com.jorge.gestorTorneos.repository.UsuarioRepository;
import com.jorge.gestorTorneos.service.SesionService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthUtil {

    private static final String BEARER_PREFIX = "Bearer ";

    private final SesionService sesionService;
    private final UsuarioRepository usuarioRepository;

    public AuthUtil(SesionService sesionService, UsuarioRepository usuarioRepository) {
        this.sesionService = sesionService;
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obtenerUsuarioDesdeToken(HttpServletRequest request) {
        String token = extraerBearerToken(request);
        if (!sesionService.tokenValido(token)) {
            throw new BadRequestException("Token inválido");
        }
        Integer usuarioId = sesionService.obtenerUsuarioIdPorToken(token);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BadRequestException("Usuario desactivado");
        }
        return usuario;
    }

    public void requireRole(HttpServletRequest request, RolUsuario... rolesPermitidos) {
        Usuario usuario = obtenerUsuarioDesdeToken(request);
        if (rolesPermitidos == null || rolesPermitidos.length == 0) {
            return;
        }
        boolean permitido = Arrays.stream(rolesPermitidos).anyMatch(r -> r == usuario.getRol());
        if (!permitido) {
            throw new ForbiddenException("No tienes permiso para realizar esta acción");
        }
    }

    public void requireAdminOrOwner(HttpServletRequest request, Torneo torneo) {
        Usuario usuario = obtenerUsuarioDesdeToken(request);
        if (usuario.getRol() == RolUsuario.ADMIN) {
            return;
        }
        if (usuario.getRol() == RolUsuario.ORGANIZADOR
                && torneo != null
                && torneo.getCreador() != null
                && torneo.getCreador().getId() != null
                && torneo.getCreador().getId().equals(usuario.getId())) {
            return;
        }
        throw new ForbiddenException("No tienes permiso para realizar esta acción sobre este torneo");
    }

    /**
     * ADMIN: puede inscribir a cualquier usuario. ORGANIZADOR: a cualquiera solo si es creador del torneo;
     * si no, solo puede inscribirse a sí mismo. USUARIO: solo puede inscribirse a sí mismo.
     */
    public void verificarPermisoInscripcion(Usuario solicitante, Torneo torneo, Integer usuarioIdAInscribir) {
        if (solicitante.getRol() == RolUsuario.ADMIN) {
            return;
        }
        if (solicitante.getRol() == RolUsuario.USUARIO) {
            if (!Objects.equals(solicitante.getId(), usuarioIdAInscribir)) {
                throw new ForbiddenException("Solo puedes inscribirte a ti mismo");
            }
            return;
        }
        if (solicitante.getRol() == RolUsuario.ORGANIZADOR) {
            boolean esCreador = torneo.getCreador() != null
                    && Objects.equals(torneo.getCreador().getId(), solicitante.getId());
            if (esCreador) {
                return;
            }
            if (Objects.equals(solicitante.getId(), usuarioIdAInscribir)) {
                return;
            }
            throw new ForbiddenException("Solo el creador del torneo puede inscribir a otros jugadores");
        }
        throw new ForbiddenException("No tienes permiso para realizar esta inscripción");
    }

    private static String extraerBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            throw new BadRequestException("Token no enviado");
        }
        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new BadRequestException("Token inválido");
        }
        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new BadRequestException("Token no enviado");
        }
        return token;
    }
}
