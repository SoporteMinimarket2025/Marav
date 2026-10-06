package com.ecomarket.controller;

import com.ecomarket.model.Abono;
import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.AbonoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * 💰 ABONO CONTROLLER | ECOMARKET PRO
 * ============================================================
 *
 * Controlador del módulo de Abonos.
 *
 * ============================================================
 * 🔐 SEGURIDAD MULTI-NEGOCIO
 * ============================================================
 *
 * El negocio se obtiene exclusivamente desde:
 *
 * Authentication
 *       ↓
 * CustomUserDetails
 *       ↓
 * getNegocioId()
 *
 * Nunca confiamos en negocioId enviado por el navegador.
 *
 * ============================================================
 */
@Controller
public class AbonoController {


    private final AbonoService abonoService;


    public AbonoController(
            AbonoService abonoService
    ) {

        this.abonoService = abonoService;
    }


    // ========================================================
    // 📄 VISTA ABONOS
    // ========================================================

    @GetMapping("/abonos")
    public String mostrarAbonos(
            Authentication auth,
            Model model
    ) {

        CustomUserDetails user =
                obtenerUsuario(auth);

        model.addAttribute(
                "usuario",
                user.getNombre()
        );

        model.addAttribute(
                "negocioId",
                user.getNegocioId()
        );

        model.addAttribute(
                "rolUsuario",
                user.getRol()
        );

        model.addAttribute(
                "menuActivo",
                "abonos"
        );

        return "abonos";
    }


    // ========================================================
    // 📋 LISTAR ABONOS
    // ========================================================

    @GetMapping("/api/abonos")
    @ResponseBody
    public ResponseEntity<?> listar(
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            List<Abono> abonos =
                    abonoService.listarTodos(
                            negocioId
                    );

            return ResponseEntity.ok(abonos);

        } catch (Exception e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // 🔍 OBTENER ABONO
    // ========================================================

    @GetMapping("/api/abonos/{id}")
    @ResponseBody
    public ResponseEntity<?> obtener(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            return ResponseEntity.ok(
                    abonoService.buscarPorId(
                            id,
                            negocioId
                    )
            );

        } catch (Exception e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // 👥 CLIENTES CON DEUDA
    // ========================================================

    @GetMapping("/api/abonos/clientes-deuda")
    @ResponseBody
    public ResponseEntity<?> clientesConDeuda(
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            return ResponseEntity.ok(
                    abonoService.listarClientesConDeuda(
                            negocioId
                    )
            );

        } catch (Exception e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // 🧾 FACTURAS DEL CLIENTE
    // ========================================================

    @GetMapping(
            "/api/abonos/facturas-cliente/{idCliente}"
    )
    @ResponseBody
    public ResponseEntity<?> facturasCliente(
            @PathVariable Integer idCliente,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            return ResponseEntity.ok(
                    abonoService.listarFacturasDeuda(
                            idCliente,
                            negocioId
                    )
            );

        } catch (Exception e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // ➕ CREAR ABONO
    // ========================================================

    @PostMapping("/api/abonos")
    @ResponseBody
    public ResponseEntity<?> crear(
            @RequestBody Abono abono,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            Abono guardado =
                    abonoService.crear(
                            abono,
                            negocioId
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(guardado);

        } catch (Exception e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // ✏️ ACTUALIZAR ABONO
    // ========================================================

    @PutMapping("/api/abonos/{id}")
    @ResponseBody
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestBody Abono abono,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            Abono actualizado =
                    abonoService.actualizar(
                            id,
                            abono,
                            negocioId
                    );

            return ResponseEntity.ok(
                    actualizado
            );

        } catch (Exception e) {

            return error(
                    e.getMessage()
            );
        }
    }


    // ========================================================
    // 🗑️ ELIMINAR ABONO
    // ========================================================

    @DeleteMapping("/api/abonos/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            abonoService.eliminar(
                    id,
                    negocioId
            );

            Map<String, Object> respuesta =
                    new HashMap<>();

            respuesta.put(
                    "message",
                    "Abono eliminado correctamente."
            );

            return ResponseEntity.ok(
                    respuesta
            );

        } catch (Exception e) {

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

        if (auth == null ||
                !(auth.getPrincipal()
                        instanceof CustomUserDetails)) {

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

