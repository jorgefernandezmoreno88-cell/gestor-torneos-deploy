package com.jorge.gestorTorneos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jorge.gestorTorneos.dto.request.ActualizarTorneoRequest;

import jakarta.validation.Valid;
import com.jorge.gestorTorneos.dto.response.TorneoPageResponse;
import com.jorge.gestorTorneos.dto.response.TorneoResponse;
import com.jorge.gestorTorneos.dto.response.TorneoResumenResponse;
import com.jorge.gestorTorneos.dto.request.CambiarEstadoTorneoRequest;
import com.jorge.gestorTorneos.dto.request.CrearTorneoRequest;
import com.jorge.gestorTorneos.model.entity.Torneo;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.model.enums.EstadoTorneo;
import com.jorge.gestorTorneos.model.enums.FormatoTorneo;
import com.jorge.gestorTorneos.model.enums.NivelTorneo;
import com.jorge.gestorTorneos.model.enums.RolUsuario;
import com.jorge.gestorTorneos.service.TorneoService;
import com.jorge.gestorTorneos.util.AuthUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    private final TorneoService torneoService;
    private final AuthUtil authUtil;

    public TorneoController(TorneoService torneoService, AuthUtil authUtil) {
        this.torneoService = torneoService;
        this.authUtil = authUtil;
    }

    @GetMapping
    public Object listar(@RequestParam(required = false) EstadoTorneo estado,
                         @RequestParam(required = false) NivelTorneo nivel,
                         @RequestParam(required = false) FormatoTorneo formato,
                         @RequestParam(required = false) Integer page,
                         @RequestParam(required = false) Integer size) {
        boolean usarPaginacion = estado != null || nivel != null || formato != null
                || page != null || size != null;
        if (!usarPaginacion) {
            return torneoService.listarTodos()
                    .stream()
                    .map(TorneoResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 10;
        if (pageNum < 0) {
            pageNum = 0;
        }
        if (sizeNum < 1) {
            sizeNum = 10;
        }

        Page<Torneo> resultado = torneoService.listarConFiltros(estado, nivel, formato, pageNum, sizeNum);
        return TorneoPageResponse.fromPage(resultado);
    }

    @GetMapping("/{id}/resumen")
    public TorneoResumenResponse resumen(HttpServletRequest httpRequest, @PathVariable Integer id) {
        authUtil.requireRole(httpRequest, RolUsuario.USUARIO, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        return torneoService.obtenerResumen(id);
    }

    @GetMapping("/{id}")
    public TorneoResponse obtener(@PathVariable Integer id) {
        return TorneoResponse.fromEntity(torneoService.obtenerPorId(id));
    }

    @PostMapping
    public TorneoResponse crear(HttpServletRequest httpRequest, @Valid @RequestBody CrearTorneoRequest request) {
        authUtil.requireRole(httpRequest, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Usuario creador = authUtil.obtenerUsuarioDesdeToken(httpRequest);
        return TorneoResponse.fromEntity(torneoService.crearTorneo(request, creador));
    }

    @PutMapping("/{id}")
    public TorneoResponse actualizar(HttpServletRequest httpRequest, @PathVariable Integer id,
                                     @Valid @RequestBody ActualizarTorneoRequest request) {
        authUtil.requireRole(httpRequest, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Torneo torneo = torneoService.obtenerPorId(id);
        authUtil.requireAdminOrOwner(httpRequest, torneo);
        return TorneoResponse.fromEntity(torneoService.actualizarTorneo(id, request));
    }

    @PatchMapping("/{id}/estado")
    public TorneoResponse cambiarEstado(HttpServletRequest httpRequest, @PathVariable Integer id,
                                        @RequestBody CambiarEstadoTorneoRequest request) {
        authUtil.requireRole(httpRequest, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Torneo torneo = torneoService.obtenerPorId(id);
        authUtil.requireAdminOrOwner(httpRequest, torneo);
        return TorneoResponse.fromEntity(torneoService.cambiarEstado(id, request));
    }

    @PatchMapping("/{id}/abrir")
    public TorneoResponse abrir(HttpServletRequest httpRequest, @PathVariable Integer id) {
        authUtil.requireRole(httpRequest, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Torneo torneo = torneoService.obtenerPorId(id);
        authUtil.requireAdminOrOwner(httpRequest, torneo);
        return TorneoResponse.fromEntity(torneoService.abrirTorneo(id));
    }

    @PatchMapping("/{id}/cerrar")
    public TorneoResponse cerrar(HttpServletRequest httpRequest, @PathVariable Integer id) {
        authUtil.requireRole(httpRequest, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Torneo torneo = torneoService.obtenerPorId(id);
        authUtil.requireAdminOrOwner(httpRequest, torneo);
        return TorneoResponse.fromEntity(torneoService.cerrarTorneo(id));
    }

    @PatchMapping("/{id}/iniciar")
    public TorneoResponse iniciar(HttpServletRequest httpRequest, @PathVariable Integer id) {
        authUtil.requireRole(httpRequest, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Torneo torneo = torneoService.obtenerPorId(id);
        authUtil.requireAdminOrOwner(httpRequest, torneo);
        return TorneoResponse.fromEntity(torneoService.iniciarTorneo(id));
    }

    @PatchMapping("/{id}/finalizar")
    public TorneoResponse finalizar(HttpServletRequest httpRequest, @PathVariable Integer id) {
        authUtil.requireRole(httpRequest, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Torneo torneo = torneoService.obtenerPorId(id);
        authUtil.requireAdminOrOwner(httpRequest, torneo);
        return TorneoResponse.fromEntity(torneoService.finalizarTorneo(id));
    }

    @PatchMapping("/{id}/cancelar")
    public TorneoResponse cancelar(HttpServletRequest httpRequest, @PathVariable Integer id) {
        authUtil.requireRole(httpRequest, RolUsuario.ORGANIZADOR, RolUsuario.ADMIN);
        Torneo torneo = torneoService.obtenerPorId(id);
        authUtil.requireAdminOrOwner(httpRequest, torneo);
        return TorneoResponse.fromEntity(torneoService.cancelarTorneo(id));
    }
}