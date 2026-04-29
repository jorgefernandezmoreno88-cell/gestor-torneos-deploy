package com.jorge.gestorTorneos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.jorge.gestorTorneos.dto.response.TorneoBusquedaResponse;

@Service
public class BusquedaSemanticaService {

    private final JdbcTemplate jdbcTemplate;
    private final OpenAIEmbeddingService openAIEmbeddingService;

    public BusquedaSemanticaService(
            JdbcTemplate jdbcTemplate,
            OpenAIEmbeddingService openAIEmbeddingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.openAIEmbeddingService = openAIEmbeddingService;
    }

    public void indexarTorneos() {
        String sqlSelect = """
                select id, nombre, descripcion, formato, nivel
                from torneos
                """;

        List<Long> ids = jdbcTemplate.query(sqlSelect, (rs, rowNum) -> {
            Long id = rs.getLong("id");

            String texto = """
                    Nombre: %s
                    Descripción: %s
                    Formato: %s
                    Nivel: %s
                    """.formatted(
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getString("formato"),
                    rs.getString("nivel")
            );

            List<Double> embedding = openAIEmbeddingService.generarEmbedding(texto);
            String vector = convertirAVectorPostgres(embedding);

            jdbcTemplate.update(
                    "update torneos set embedding = ?::vector where id = ?",
                    vector,
                    id
            );

            return id;
        });

        System.out.println("Torneos indexados: " + ids.size());
    }

    public List<TorneoBusquedaResponse> buscar(String consulta) {
        List<Double> embedding = openAIEmbeddingService.generarEmbedding(consulta);
        String vector = convertirAVectorPostgres(embedding);

        String sql = """
                select 
                    id,
                    nombre,
                    descripcion,
                    formato,
                    nivel,
                    1 - (embedding <=> ?::vector) as similitud
                from torneos
                where embedding is not null
                order by embedding <=> ?::vector
                limit 10
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new TorneoBusquedaResponse(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("formato"),
                        rs.getString("nivel"),
                        rs.getDouble("similitud")
                ),
                vector,
                vector
        );
    }

    private String convertirAVectorPostgres(List<Double> embedding) {
        return embedding.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }
}