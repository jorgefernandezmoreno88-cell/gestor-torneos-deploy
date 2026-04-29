package com.jorge.gestorTorneos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import com.jorge.gestorTorneos.dto.request.CambiarRolUsuarioRequest;
import com.jorge.gestorTorneos.dto.request.CrearUsuarioRequest;
import com.jorge.gestorTorneos.dto.response.ClasificacionResponse;
import com.jorge.gestorTorneos.dto.response.PartidaResponse;
import com.jorge.gestorTorneos.dto.response.TorneoResponse;
import com.jorge.gestorTorneos.dto.response.UsuarioResumenResponse;
import com.jorge.gestorTorneos.dto.response.UsuarioResponse;
import com.jorge.gestorTorneos.model.enums.RolUsuario;
import com.jorge.gestorTorneos.service.UsuarioService;
import com.jorge.gestorTorneos.util.AuthUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthUtil authUtil;

    public UsuarioController(UsuarioService usuarioService, AuthUtil authUtil) {
        this.usuarioService = usuarioService;
        this.authUtil = authUtil;
    }

    @PostMapping
    public UsuarioResponse crear(@Valid @RequestBody CrearUsuarioRequest request) {
        return UsuarioResponse.fromEntity(usuarioService.crearUsuario(request));
    }

    @GetMapping
    public List<UsuarioResponse> listar(HttpServletRequest request) {
        authUtil.requireRole(request, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return usuarioService.listarTodos()
                .stream()
                .map(UsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{usuarioId}/torneos-inscrito")
    public List<TorneoResponse> listarTorneosInscrito(@PathVariable Integer usuarioId) {
        return usuarioService.listarTorneosInscrito(usuarioId).stream()
                .map(TorneoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{usuarioId}/partidas")
    public List<PartidaResponse> listarPartidasUsuario(@PathVariable Integer usuarioId) {
        return usuarioService.listarPartidas(usuarioId).stream()
                .map(PartidaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{usuarioId}/clasificaciones")
    public List<ClasificacionResponse> listarClasificacionesUsuario(@PathVariable Integer usuarioId) {
        return usuarioService.listarClasificaciones(usuarioId).stream()
                .map(ClasificacionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{usuarioId}/resumen")
    public UsuarioResumenResponse resumenUsuario(@PathVariable Integer usuarioId) {
        return usuarioService.obtenerResumen(usuarioId);
    }
    
    @GetMapping("/{id}")
    public UsuarioResponse obtener(HttpServletRequest request, @PathVariable Integer id) {
        authUtil.requireRole(request, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return UsuarioResponse.fromEntity(usuarioService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/desactivar")
    public UsuarioResponse desactivar(HttpServletRequest request, @PathVariable Integer id) {
        authUtil.requireRole(request, RolUsuario.ADMIN);
        return UsuarioResponse.fromEntity(usuarioService.desactivar(id));
    }

    @PatchMapping("/{id}/activar")
    public UsuarioResponse activar(HttpServletRequest request, @PathVariable Integer id) {
        authUtil.requireRole(request, RolUsuario.ADMIN);
        return UsuarioResponse.fromEntity(usuarioService.activar(id));
    }

    @PatchMapping("/{id}/rol")
    public UsuarioResponse cambiarRol(HttpServletRequest request,
                                      @PathVariable Integer id,
                                      @RequestBody CambiarRolUsuarioRequest body) {
        authUtil.requireRole(request, RolUsuario.ADMIN);
        return UsuarioResponse.fromEntity(usuarioService.cambiarRol(id, body != null ? body.getRol() : null));
    }
}