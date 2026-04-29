package com.jorge.gestorTorneos.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jorge.gestorTorneos.dto.response.ClasificacionResponse;
import com.jorge.gestorTorneos.model.enums.RolUsuario;
import com.jorge.gestorTorneos.service.ClasificacionService;
import com.jorge.gestorTorneos.util.AuthUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ClasificacionController {

    private final ClasificacionService clasificacionService;
    private final AuthUtil authUtil;

    public ClasificacionController(ClasificacionService clasificacionService, AuthUtil authUtil) {
        this.clasificacionService = clasificacionService;
        this.authUtil = authUtil;
    }

    @GetMapping("/api/torneos/{torneoId}/clasificacion")
    public List<ClasificacionResponse> listar(HttpServletRequest request, @PathVariable Integer torneoId) {
        return clasificacionService.listarPorTorneo(torneoId)
                .stream()
                .map(ClasificacionResponse::fromEntity)
                .collect(Collectors.toList());
    }
}