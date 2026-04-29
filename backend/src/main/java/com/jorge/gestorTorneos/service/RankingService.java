package com.jorge.gestorTorneos.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jorge.gestorTorneos.dto.response.RankingGlobalResponse;
import com.jorge.gestorTorneos.model.entity.Clasificacion;
import com.jorge.gestorTorneos.model.entity.Usuario;
import com.jorge.gestorTorneos.repository.ClasificacionRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RankingService {

    private final ClasificacionRepository clasificacionRepository;

    @Transactional(readOnly = true)
    public List<RankingGlobalResponse> rankingGlobal() {
        List<Clasificacion> todas = clasificacionRepository.findAllWithUsuario();

        Map<Integer, AcumuladorRanking> porUsuario = new HashMap<>();
        for (Clasificacion c : todas) {
            Usuario u = c.getUsuario();
            if (u == null || u.getId() == null) {
                continue;
            }
            AcumuladorRanking acc = porUsuario.computeIfAbsent(u.getId(), id -> new AcumuladorRanking(u));
            acc.totalPuntos += valor(c.getPuntos());
            acc.totalVictorias += valor(c.getVictorias());
            acc.totalDerrotas += valor(c.getDerrotas());
            acc.totalEmpates += valor(c.getEmpates());
            acc.totalPartidasJugadas += valor(c.getPartidasJugadas());
            acc.totalTorneos++;
        }

        List<AcumuladorRanking> lista = new ArrayList<>(porUsuario.values());
        lista.sort(Comparator
                .comparingInt(AcumuladorRanking::getTotalPuntos).reversed()
                .thenComparing(Comparator.comparingInt(AcumuladorRanking::getTotalVictorias).reversed())
                .thenComparing(Comparator.comparingInt(AcumuladorRanking::getTotalEmpates).reversed())
                .thenComparingInt(AcumuladorRanking::getTotalDerrotas));

        List<RankingGlobalResponse> resultado = new ArrayList<>(lista.size());
        for (int i = 0; i < lista.size(); i++) {
            AcumuladorRanking acc = lista.get(i);
            Usuario u = acc.usuario;
            resultado.add(RankingGlobalResponse.builder()
                    .posicion(i + 1)
                    .usuarioId(u.getId())
                    .nombre(u.getNombre())
                    .email(u.getEmail())
                    .puntos(acc.totalPuntos)
                    .victorias(acc.totalVictorias)
                    .empates(acc.totalEmpates)
                    .derrotas(acc.totalDerrotas)
                    .partidasJugadas(acc.totalPartidasJugadas)
                    .torneosJugados(acc.totalTorneos)
                    .build());
        }
        return resultado;
    }

    private static int valor(Integer v) {
        return v != null ? v : 0;
    }

    private static final class AcumuladorRanking {
        private final Usuario usuario;
        private int totalPuntos;
        private int totalVictorias;
        private int totalDerrotas;
        private int totalEmpates;
        private int totalPartidasJugadas;
        private int totalTorneos;

        private AcumuladorRanking(Usuario usuario) {
            this.usuario = usuario;
        }

        int getTotalPuntos() {
            return totalPuntos;
        }

        int getTotalVictorias() {
            return totalVictorias;
        }

        int getTotalPartidasJugadas() {
            return totalPartidasJugadas;
        }

        int getTotalEmpates() {
            return totalEmpates;
        }

        int getTotalDerrotas() {
            return totalDerrotas;
        }
    }
}
