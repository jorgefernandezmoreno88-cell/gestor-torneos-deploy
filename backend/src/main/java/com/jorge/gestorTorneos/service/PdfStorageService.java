package com.jorge.gestorTorneos.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import com.jorge.gestorTorneos.config.SupabaseStorageProperties;

@Service
public class PdfStorageService {

    private final SupabaseStorageProperties properties;
    private final RestClient restClient;

    public PdfStorageService(SupabaseStorageProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder().build();
    }

    public String subirPdfClasificacion(Long torneoId, MultipartFile file) {
        try {
            String fecha = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String path = "torneos/" + torneoId + "/clasificacion_" + fecha + ".pdf";

            String uploadUrl = properties.getUrl()
                    + "/storage/v1/object/"
                    + properties.getStorage().getBucket()
                    + "/"
                    + path;
            
            System.out.println("Subiendo PDF a: " + uploadUrl);
            System.out.println("Bucket: " + properties.getStorage().getBucket());
            System.out.println("Supabase URL: " + properties.getUrl());
            System.out.println("Service key cargada: " + (properties.getServiceRoleKey() != null));

            restClient.post()
                    .uri(uploadUrl)
                    .header("apikey", properties.getServiceRoleKey())
                    .header("Authorization", "Bearer " + properties.getServiceRoleKey())
                    .header("x-upsert", "true")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(file.getBytes())
                    .retrieve()
                    .body(String.class);

            String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8)
                    .replace("+", "%20")
                    .replace("%2F", "/");

            return properties.getUrl()
                    + "/storage/v1/object/public/"
                    + properties.getStorage().getBucket()
                    + "/"
                    + encodedPath;

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("ERROR SUPABASE STATUS: " + e.getStatusCode());
            System.err.println("ERROR SUPABASE BODY: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error Supabase Storage: " + e.getResponseBodyAsString(), e);

        } catch (org.springframework.web.client.HttpServerErrorException e) {
            System.err.println("ERROR SUPABASE STATUS: " + e.getStatusCode());
            System.err.println("ERROR SUPABASE BODY: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error Supabase Storage: " + e.getResponseBodyAsString(), e);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error subiendo PDF a Supabase Storage", e);
        }
    }
}