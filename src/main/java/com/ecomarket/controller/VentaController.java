// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.controller;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.dto.FacturaDTO;
import com.ecomarket.dto.VentaDTO;

import com.ecomarket.model.Venta;

import com.ecomarket.security.CustomUserDetails;

import com.ecomarket.service.VentaService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ======================================================
 * 🛒 CONTROLADOR API VENTAS
 * ======================================================
 *
 * ✔ Crear ventas
 * ✔ Validar usuario autenticado
 * ✔ Obtener factura
 * ✔ Compatible POS
 * ✔ Compatible carrito dinámico
 * ✔ Compatible ventas a crédito
 * ✔ Aislamiento por negocio
 *
 * ======================================================
 */
@RestController
@RequestMapping("/api/ventas")
@CrossOrigin
public class VentaController {

    // ==================================================
    // 🔗 SERVICE
    // ==================================================
    @Autowired
    private VentaService service;


    // ==================================================
    // 💾 CREAR VENTA
    // ==================================================
    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody VentaDTO dto,
            Authentication auth
    ) {

        try {

            // ==========================================
            // 🔐 VALIDAR LOGIN
            // ==========================================
            if (
                    auth == null
                            || auth.getPrincipal() == null
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Usuario no autenticado"
                        );
            }


            // ==========================================
            // 👤 USUARIO LOGUEADO
            // ==========================================
            CustomUserDetails user =
                    (CustomUserDetails)
                            auth.getPrincipal();


            // ==========================================
            // 👤 ASIGNAR USUARIO
            // ==========================================
            //
            // NO confiamos en el usuario enviado
            // desde JavaScript.
            //
            dto.setUsuarioId(
                    user.getId()
            );


            // ==========================================
            // 🏪 OBTENER NEGOCIO
            // ==========================================
            Integer negocioId =
                    user.getNegocioId();


            if (negocioId == null) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "El usuario no tiene un negocio asociado."
                        );
            }


            // ==========================================
            // 💾 CREAR VENTA
            // ==========================================
            Venta venta =
                    service.crearVenta(
                            dto,
                            negocioId
                    );


            // ==========================================
            // 🧾 GENERAR FACTURA
            // ==========================================
            FacturaDTO factura =
                    service.obtenerFactura(
                            venta.getId(),
                            negocioId
                    );


            // ==========================================
            // ✅ RESPUESTA
            // ==========================================
            return ResponseEntity.ok(
                    factura
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Error creando venta: "
                                    + e.getMessage()
                    );
        }
    }


    // ==================================================
    // 🧾 OBTENER FACTURA
    // ==================================================
    @GetMapping("/{id}/factura")
    public ResponseEntity<?> factura(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            // ==========================================
            // 🔐 USUARIO
            // ==========================================
            CustomUserDetails user =
                    (CustomUserDetails)
                            auth.getPrincipal();


            Integer negocioId =
                    user.getNegocioId();


            // ==========================================
            // 🧾 OBTENER FACTURA
            // ==========================================
            FacturaDTO factura =
                    service.obtenerFactura(
                            id,
                            negocioId
                    );


            return ResponseEntity.ok(
                    factura
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Error obteniendo factura: "
                                    + e.getMessage()
                    );
        }
    }


    // ==================================================
    // 🧾 LISTAR VENTAS
    // ==================================================
    @GetMapping
    public ResponseEntity<List<Venta>> listar(
            Authentication auth
    ) {

        CustomUserDetails user =
                (CustomUserDetails)
                        auth.getPrincipal();


        Integer negocioId =
                user.getNegocioId();


        return ResponseEntity.ok(
                service.listarTodas(
                        negocioId
                )
        );
    }
}