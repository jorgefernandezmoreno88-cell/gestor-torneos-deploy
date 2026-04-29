package com.jorge.gestorTorneos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jorge.gestorTorneos.dto.response.TorneoBusquedaResponse;
import com.jorge.gestorTorneos.service.BusquedaSemanticaService;

@RestController
@RequestMapping("/api/busqueda")
public class BusquedaSemanticaController {

    private final BusquedaSemanticaService busquedaSemanticaService;

    public BusquedaSemanticaController(BusquedaSemanticaService busquedaSemanticaService) {
        this.busquedaSemanticaService = busquedaSemanticaService;
    }

    @PatchMapping("/indexar-torneos")
    public String indexarTorneos() {
        busquedaSemanticaService.indexarTorneos();
        return "Torneos indexados correctamente";
    }

    @GetMapping("/torneos")
    public List<TorneoBusquedaResponse> buscarTorneos(@RequestParam String q) {
        return busquedaSemanticaService.buscar(q);
    }
}