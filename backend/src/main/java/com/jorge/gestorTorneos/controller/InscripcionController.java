package com.jorge.gestorTorneos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.jorge.gestorTorneos.dto.request.InscribirUsuarioRequest;
import com.jorge.gestorTorneos.dto.response.InscripcionResponse;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.RolUsuario;
import com.jorge.gestorTorneos.service.InscripcionService;
import com.jorge.gestorTorneos.util.AuthUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final AuthUtil authUtil;

    public InscripcionController(InscripcionService inscripcionService, AuthUtil authUtil) {
        this.inscripcionService = inscripcionService;
        this.authUtil = authUtil;
    }

    @PostMapping("/api/torneos/{torneoId}/inscripciones")
    public InscripcionResponse inscribir(HttpServletRequest httpRequest,
                                         @PathVariable Integer torneoId,
                                         @RequestBody InscribirUsuarioRequest request) {
        authUtil.requireRole(httpRequest, RolUsuario.USUARIO, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Usuario solicitante = authUtil.obtenerUsuarioDesdeToken(httpRequest);
        return InscripcionResponse.fromEntity(
                inscripcionService.inscribirUsuario(torneoId, request.getUsuarioId(), solicitante));
    }

    @GetMapping("/api/torneos/{torneoId}/inscripciones")
    public List<InscripcionResponse> listarPorTorneo(HttpServletRequest request,
                                                     @PathVariable Integer torneoId) {
 
        return inscripcionService.listarPorTorneo(torneoId)
                .stream()
                .map(InscripcionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PatchMapping("/api/inscripciones/{id}/retiro")
    public InscripcionResponse marcarRetiro(HttpServletRequest request, @PathVariable Integer id) {
        authUtil.requireRole(request, RolUsuario.USUARIO, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return InscripcionResponse.fromEntity(inscripcionService.marcarRetiro(id));
    }

    @PatchMapping("/api/inscripciones/{inscripcionId}/retirar")
    public InscripcionResponse retirar(HttpServletRequest request, @PathVariable Integer inscripcionId) {
        authUtil.requireRole(request, RolUsuario.USUARIO, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return InscripcionResponse.fromEntity(inscripcionService.retirarInscripcion(inscripcionId));
    }

    @PatchMapping("/api/inscripciones/{inscripcionId}/cancelar")
    public InscripcionResponse cancelar(HttpServletRequest request, @PathVariable Integer inscripcionId) {
        authUtil.requireRole(request, RolUsuario.USUARIO, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return InscripcionResponse.fromEntity(inscripcionService.cancelarInscripcion(inscripcionId));
    }
}