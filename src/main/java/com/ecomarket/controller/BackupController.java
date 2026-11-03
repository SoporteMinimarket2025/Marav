
package com.ecomarket.controller;

import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.ExcelBackupService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/backup")
public class BackupController {

    private final ExcelBackupService excelBackupService;

    public BackupController(ExcelBackupService excelBackupService) {
        this.excelBackupService = excelBackupService;
    }

    // ======================================================
    // DESCARGAR RESPALDO EXCEL
    // SOLAMENTE ADMINISTRADOR
    // ======================================================

    @GetMapping("/excel")
    public ResponseEntity<byte[]> descargarExcel(
            Authentication authentication) throws IOException {

        // Obtener el usuario autenticado
        CustomUserDetails usuario =
                (CustomUserDetails) authentication.getPrincipal();

        // Obtener el negocio desde la sesión autenticada
        Integer negocioId = usuario.getNegocioId();

        // Generar archivo Excel del negocio correspondiente
        byte[] archivo =
                excelBackupService.generarExcel(negocioId);

        // Nombre del archivo con fecha y hora
        String fecha = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        );

        String nombreArchivo = "MARAV_Respaldo_" + fecha + ".xlsx";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nombreArchivo + "\""
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(archivo);
    }
}
