package com.jorge.gestorTorneos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.jorge.gestorTorneos.dto.request.RegistrarResultadoPartidaRequest;

import jakarta.validation.Valid;
import com.jorge.gestorTorneos.dto.response.EnfrentamientoExisteResponse;
import com.jorge.gestorTorneos.dto.response.PartidaResponse;
import com.jorge.gestorTorneos.model.entity.Partida;
import com.jorge.gestorTorneos.model.enums.RolUsuario;
import com.jorge.gestorTorneos.service.PartidaService;
import com.jorge.gestorTorneos.util.AuthUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class PartidaController {

    private final PartidaService partidaService;
    private final AuthUtil authUtil;

    public PartidaController(PartidaService partidaService, AuthUtil authUtil) {
        this.partidaService = partidaService;
        this.authUtil = authUtil;
    }

    @GetMapping("/api/torneos/{torneoId}/partidas")
    public List<PartidaResponse> listarPorTorneo(HttpServletRequest request,
                                                   @PathVariable Integer torneoId) {
       
        return partidaService.listarPorTorneo(torneoId)
                .stream()
                .map(PartidaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/torneos/{torneoId}/enfrentamientos/existe")
    public EnfrentamientoExisteResponse existeEnfrentamiento(HttpServletRequest request,
                                                             @PathVariable Integer torneoId,
                                                             @RequestParam Integer usuario1Id,
                                                             @RequestParam Integer usuario2Id) {
        authUtil.requireRole(request, RolUsuario.USUARIO, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return partidaService.existeEnfrentamiento(torneoId, usuario1Id, usuario2Id);
    }

    @GetMapping("/api/torneos/{torneoId}/enfrentamientos")
    public List<PartidaResponse> listarEnfrentamientos(HttpServletRequest request,
                                                       @PathVariable Integer torneoId,
                                                       @RequestParam Integer usuario1Id,
                                                       @RequestParam Integer usuario2Id) {
        authUtil.requireRole(request, RolUsuario.USUARIO, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return partidaService.listarEnfrentamientos(torneoId, usuario1Id, usuario2Id)
                .stream()
                .map(PartidaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PatchMapping("/api/partidas/{partidaId}/resultado")
    public PartidaResponse registrarResultado(HttpServletRequest httpRequest,
                                              @PathVariable Integer partidaId,
                                              @Valid @RequestBody RegistrarResultadoPartidaRequest request) {
        authUtil.requireRole(httpRequest, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Partida partida = partidaService.obtenerPorId(partidaId);
        authUtil.requireAdminOrOwner(httpRequest, partida.getTorneo());
        return PartidaResponse.fromEntity(partidaService.registrarResultado(partidaId, request));
    }

    @PatchMapping("/api/partidas/{id}/validar")
    public PartidaResponse validar(HttpServletRequest request, @PathVariable Integer id) {
        authUtil.requireRole(request, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return PartidaResponse.fromEntity(partidaService.validar(id));
    }
}
