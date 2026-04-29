package com.jorge.gestorTorneos.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class SesionService {

    private final Map<String, Integer> sesionesPorToken = new ConcurrentHashMap<>();

    public String crearSesion(Integer usuarioId) {
        String token = UUID.randomUUID().toString();
        sesionesPorToken.put(token, usuarioId);
        return token;
    }

    public Integer obtenerUsuarioIdPorToken(String token) {
        return sesionesPorToken.get(token);
    }

    public void cerrarSesion(String token) {
        sesionesPorToken.remove(token);
    }

    public boolean tokenValido(String token) {
        return token != null && sesionesPorToken.containsKey(token);
    }
}
