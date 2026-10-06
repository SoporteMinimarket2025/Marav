package com.ecomarket.controller;

import com.ecomarket.model.Devolucion;
import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.DevolucionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * ↩️ API DEVOLUCIONES | ECOMARKET PRO
 * ============================================================
 *
 * Controlador REST del módulo Devoluciones.
 *
 * ============================================================
 * 🔐 SEGURIDAD MULTI-NEGOCIO
 * ============================================================
 *
 * El negocio NO se recibe desde:
 *
 * ❌ HTML
 * ❌ JavaScript
 * ❌ RequestBody
 * ❌ PathVariable
 *
 * Se obtiene exclusivamente desde:
 *
 * Authentication
 *       ↓
 * CustomUserDetails
 *       ↓
 * getNegocioId()
 *
 * ============================================================
 */
@RestController
@RequestMapping("/api/devoluciones")
@CrossOrigin("*")
public class DevolucionController {


    private final DevolucionService devolucionService;


    public DevolucionController(
            DevolucionService devolucionService
    ) {

        this.devolucionService =
                devolucionService;
    }


    // ========================================================
    // 📋 LISTAR
    // ========================================================

    @GetMapping
    public ResponseEntity<?> listar(
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);


            List<Devolucion> devoluciones =
                    devolucionService.listar(
                            negocioId
                    );


            return ResponseEntity.ok(
                    devoluciones
            );

        } catch (Exception e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // 🔍 BUSCAR
    // ========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);


            Devolucion devolucion =
                    devolucionService.buscarPorId(
                            id,
                            negocioId
                    );


            if (devolucion == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            return ResponseEntity.ok(
                    devolucion
            );

        } catch (Exception e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // 💾 GUARDAR
    // ========================================================

    @PostMapping
    public ResponseEntity<?> guardar(
            @RequestBody Devolucion devolucion,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);


            Devolucion guardada =
                    devolucionService.guardar(
                            devolucion,
                            negocioId
                    );


            return ResponseEntity.ok(
                    guardada
            );

        } catch (RuntimeException e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // ✏️ ACTUALIZAR
    // ========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestBody Devolucion devolucion,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);


            Devolucion actualizada =
                    devolucionService.actualizar(
                            id,
                            devolucion,
                            negocioId
                    );


            if (actualizada == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            return ResponseEntity.ok(
                    actualizada
            );

        } catch (RuntimeException e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // 🗑️ ELIMINAR
    // ========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);


            devolucionService.eliminar(
                    id,
                    negocioId
            );


            Map<String, String> respuesta =
                    new HashMap<>();


            respuesta.put(
                    "message",
                    "Devolución eliminada correctamente."
            );


            return ResponseEntity.ok(
                    respuesta
            );

        } catch (RuntimeException e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // 👤 OBTENER USUARIO AUTENTICADO
    // ========================================================

    private CustomUserDetails obtenerUsuario(
            Authentication auth
    ) {

        if (
                auth == null ||
                        !(auth.getPrincipal()
                                instanceof CustomUserDetails)
        ) {

            throw new IllegalStateException(
                    "No se pudo identificar al usuario autenticado."
            );
        }


        CustomUserDetails user =
                (CustomUserDetails)
                        auth.getPrincipal();


        if (user.getNegocioId() == null) {

            throw new IllegalStateException(
                    "El usuario no tiene un negocio asociado."
            );
        }


        return user;
    }


    // ========================================================
    // 🏢 OBTENER NEGOCIO
    // ========================================================

    private Integer obtenerNegocioId(
            Authentication auth
    ) {

        return obtenerUsuario(auth)
                .getNegocioId();
    }


    // ========================================================
    // ❌ RESPUESTA DE ERROR
    // ========================================================

    private ResponseEntity<Map<String, String>> error(
            String mensaje
    ) {

        Map<String, String> respuesta =
                new HashMap<>();


        respuesta.put(
                "message",
                mensaje == null
                        ? "Ocurrió un error."
                        : mensaje
        );


        return ResponseEntity
                .badRequest()
                .body(respuesta);
    }
}