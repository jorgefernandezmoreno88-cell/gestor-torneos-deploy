package com.jorge.gestorTorneos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.jorge.gestorTorneos.dto.response.PartidaResponse;
import com.jorge.gestorTorneos.dto.request.CrearRondaRequest;
import com.jorge.gestorTorneos.dto.response.GenerarRondaInicialResponse;
import com.jorge.gestorTorneos.dto.response.RondaResponse;
import com.jorge.gestorTorneos.model.entity.Ronda;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.enums.RolUsuario;
import com.jorge.gestorTorneos.service.RondaService;
import com.jorge.gestorTorneos.service.TorneoService;
import com.jorge.gestorTorneos.util.AuthUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class RondaController {

    private final RondaService rondaService;
    private final TorneoService torneoService;
    private final AuthUtil authUtil;

    public RondaController(RondaService rondaService, TorneoService torneoService, AuthUtil authUtil) {
        this.rondaService = rondaService;
        this.torneoService = torneoService;
        this.authUtil = authUtil;
    }

    @GetMapping("/api/torneos/{torneoId}/rondas")
    public List<RondaResponse> listarRondasPorTorneo(HttpServletRequest request,
                                                     @PathVariable Integer torneoId) {
        authUtil.requireRole(request, RolUsuario.USUARIO, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return rondaService.listarRondasPorTorneo(torneoId)
                .stream()
                .map(RondaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/rondas/{rondaId}/partidas")
    public List<PartidaResponse> listarPartidasPorRonda(HttpServletRequest request,
                                                        @PathVariable Integer rondaId) {
        authUtil.requireRole(request, RolUsuario.USUARIO, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return rondaService.listarPartidasPorRonda(rondaId)
                .stream()
                .map(PartidaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/api/torneos/{torneoId}/rondas")
    public RondaResponse crear(HttpServletRequest httpRequest,
                               @PathVariable Integer torneoId,
                               @RequestBody CrearRondaRequest request) {
        authUtil.requireRole(httpRequest, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return RondaResponse.fromEntity(rondaService.crearRonda(torneoId, request));
    }

    @PostMapping("/api/torneos/{torneoId}/generar-ronda-inicial")
    public GenerarRondaInicialResponse generarRondaInicial(HttpServletRequest request,
                                                           @PathVariable Integer torneoId) {
        authUtil.requireRole(request, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        authUtil.requireAdminOrOwner(request, torneo);
        return rondaService.generarRondaInicial(torneoId);
    }

    @PostMapping("/api/torneos/{torneoId}/generar-siguiente-ronda")
    public GenerarRondaInicialResponse generarSiguienteRonda(HttpServletRequest request,
                                                             @PathVariable Integer torneoId) {
        authUtil.requireRole(request, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        authUtil.requireAdminOrOwner(request, torneo);
        return rondaService.generarSiguienteRonda(torneoId);
    }

    @PatchMapping("/api/rondas/{rondaId}/iniciar")
    public RondaResponse iniciar(HttpServletRequest request, @PathVariable Integer rondaId) {
        authUtil.requireRole(request, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Ronda ronda = rondaService.obtenerRondaPorId(rondaId);
        authUtil.requireAdminOrOwner(request, ronda.getTorneo());
        return RondaResponse.fromEntity(rondaService.iniciarRonda(rondaId));
    }

    @PatchMapping("/api/rondas/{rondaId}/finalizar")
    public RondaResponse finalizarPorRonda(HttpServletRequest request, @PathVariable Integer rondaId) {
        authUtil.requireRole(request, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Ronda ronda = rondaService.obtenerRondaPorId(rondaId);
        authUtil.requireAdminOrOwner(request, ronda.getTorneo());
        return RondaResponse.fromEntity(rondaService.finalizarRonda(rondaId));
    }

    @PatchMapping("/api/torneos/{torneoId}/rondas/{rondaId}/finalizar")
    public RondaResponse finalizar(HttpServletRequest request,
                                   @PathVariable Integer torneoId,
                                   @PathVariable Integer rondaId) {
        authUtil.requireRole(request, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Torneo torneo = torneoService.obtenerPorId(torneoId);
        authUtil.requireAdminOrOwner(request, torneo);
        return RondaResponse.fromEntity(rondaService.finalizarRonda(torneoId, rondaId));
    }
}