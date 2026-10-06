// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.controller;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Caja;
import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.CajaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ======================================================
 * 💰 CONTROLLER CAJA
 * ======================================================
 *
 * Todas las operaciones utilizan el negocio del usuario
 * autenticado.
 *
 * No confiamos en negocioId enviado por HTML o JS.
 *
 * ======================================================
 */
@RestController
@RequestMapping("/api/caja")
@CrossOrigin
public class CajaController {


    // ==================================================
    // 📦 SERVICE
    // ==================================================

    @Autowired
    private CajaService cajaService;


    // ======================================================
    // 👤 OBTENER USUARIO LOGUEADO
    // ======================================================

    private CustomUserDetails usuarioLogueado() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        // ==================================================
        // 🔒 VALIDAR AUTENTICACIÓN
        // ==================================================

        if (authentication == null ||
                authentication.getPrincipal() == null) {

            throw new IllegalStateException(
                    "No existe un usuario autenticado."
            );
        }


        // ==================================================
        // 🔒 VALIDAR TIPO DE USUARIO
        // ==================================================

        if (!(authentication.getPrincipal()
                instanceof CustomUserDetails)) {

            throw new IllegalStateException(
                    "No se pudo identificar correctamente al usuario."
            );
        }


        return (CustomUserDetails)
                authentication.getPrincipal();
    }


    // ======================================================
    // 🏢 OBTENER NEGOCIO DEL USUARIO
    // ======================================================

    private Integer negocioIdLogueado() {

        Integer negocioId =
                usuarioLogueado().getNegocioId();


        if (negocioId == null) {

            throw new IllegalStateException(
                    "El usuario no tiene un negocio asociado."
            );
        }


        return negocioId;
    }


    // ======================================================
    // 📋 LISTAR CAJAS
    // ======================================================

    @GetMapping
    public ResponseEntity<?> listar() {

        try {

            Integer negocioId =
                    negocioIdLogueado();


            List<Caja> cajas =
                    cajaService.listar(
                            negocioId
                    );


            return ResponseEntity.ok(
                    cajas
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ======================================================
    // 💰 CAJA ABIERTA
    // ======================================================

    @GetMapping("/abierta")
    public ResponseEntity<?> abierta() {

        try {

            Integer negocioId =
                    negocioIdLogueado();


            Caja caja =
                    cajaService.obtenerCajaAbierta(
                            negocioId
                    );


            if (caja == null) {

                return ResponseEntity
                        .noContent()
                        .build();
            }


            return ResponseEntity.ok(
                    caja
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ======================================================
    // 🔎 BUSCAR CAJA POR ID
    // ======================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Integer id
    ) {

        try {

            Integer negocioId =
                    negocioIdLogueado();


            Caja caja =
                    cajaService.buscarPorId(
                            id,
                            negocioId
                    );


            if (caja == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            return ResponseEntity.ok(
                    caja
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ======================================================
    // 🔓 ABRIR CAJA
    // ======================================================

    @PostMapping("/abrir")
    public ResponseEntity<?> abrir(
            @RequestParam Double saldoInicial
    ) {

        try {

            // ==================================================
            // 🏢 OBTENER NEGOCIO DEL USUARIO
            // ==================================================

            Integer negocioId =
                    negocioIdLogueado();


            // ==================================================
            // 👤 OBTENER USUARIO AUTENTICADO
            // ==================================================

            Integer usuarioId =
                    usuarioLogueado().getId();


            // ==================================================
            // 💰 VALIDAR SALDO
            // ==================================================

            if (saldoInicial == null ||
                    saldoInicial < 0) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "El saldo inicial no puede ser negativo."
                        );
            }


            // ==================================================
            // 🔓 ABRIR CAJA
            // ==================================================

            Caja caja =
                    cajaService.abrirCaja(
                            usuarioId,
                            saldoInicial,
                            negocioId
                    );


            return ResponseEntity.ok(
                    caja
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ======================================================
    // 💵 REGISTRAR INGRESO
    // ======================================================

    @PutMapping("/ingreso")
    public ResponseEntity<?> ingreso(
            @RequestParam Double valor
    ) {

        try {

            Integer negocioId =
                    negocioIdLogueado();


            // ==================================================
            // 💰 VALIDAR VALOR
            // ==================================================

            if (valor == null ||
                    valor <= 0) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "El valor debe ser mayor que cero."
                        );
            }


            // ==================================================
            // 💵 REGISTRAR INGRESO
            // ==================================================

            Caja caja =
                    cajaService.registrarIngreso(
                            valor,
                            negocioId
                    );


            return ResponseEntity.ok(
                    caja
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ======================================================
    // 💸 REGISTRAR EGRESO
    // ======================================================

    @PutMapping("/egreso")
    public ResponseEntity<?> egreso(
            @RequestParam Double valor
    ) {

        try {

            Integer negocioId =
                    negocioIdLogueado();


            // ==================================================
            // 💰 VALIDAR VALOR
            // ==================================================

            if (valor == null ||
                    valor <= 0) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "El valor debe ser mayor que cero."
                        );
            }


            // ==================================================
            // 💸 REGISTRAR EGRESO
            // ==================================================

            Caja caja =
                    cajaService.registrarEgreso(
                            valor,
                            negocioId
                    );


            return ResponseEntity.ok(
                    caja
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ======================================================
    // 🔒 CERRAR CAJA
    // ======================================================

    @PutMapping("/cerrar")
    public ResponseEntity<?> cerrar() {

        try {

            Integer negocioId =
                    negocioIdLogueado();


            Caja caja =
                    cajaService.cerrarCaja(
                            negocioId
                    );


            return ResponseEntity.ok(
                    caja
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}