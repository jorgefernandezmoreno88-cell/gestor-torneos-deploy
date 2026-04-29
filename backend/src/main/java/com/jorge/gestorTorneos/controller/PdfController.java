package com.jorge.gestorTorneos.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jorge.gestorTorneos.service.PdfStorageService;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfStorageService pdfStorageService;

    public PdfController(PdfStorageService pdfStorageService) {
        this.pdfStorageService = pdfStorageService;
    }

    @PostMapping("/torneos/{torneoId}/clasificacion")
    public ResponseEntity<?> subirPdfClasificacion(
            @PathVariable Long torneoId,
            @RequestParam("file") MultipartFile file
    ) {
        String url = pdfStorageService.subirPdfClasificacion(torneoId, file);

        return ResponseEntity.ok(Map.of(
                "mensaje", "PDF guardado correctamente en Supabase Storage",
                "url", url
        ));
    }
}