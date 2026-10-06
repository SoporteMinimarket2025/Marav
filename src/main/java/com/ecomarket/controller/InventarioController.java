// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.controller;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Inventario;
import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.InventarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ======================================================
// 🚀 CONTROLADOR REST INVENTARIO (PRO)
// ======================================================

/**
 * ======================================================
 * 📦 API INVENTARIO
 * ======================================================
 *
 * ✔ Listar inventario
 * ✔ Buscar inventario por ID
 * ✔ Crear inventario
 * ✔ Actualizar inventario
 * ✔ Eliminar inventario
 * ✔ Consultar stock bajo
 * ✔ Aumentar stock
 * ✔ Aislamiento por negocio
 *
 * ======================================================
 *
 * 🔐 REGLA MULTI-NEGOCIO
 *
 * Inventario no tiene negocioId directamente.
 *
 * La pertenencia se determina mediante:
 *
 * Inventario
 *      ↓
 * productoId
 *      ↓
 * Producto
 *      ↓
 * negocioId
 *
 * El negocio se obtiene SIEMPRE desde:
 *
 * Authentication
 *      ↓
 * CustomUserDetails
 *      ↓
 * negocioId
 *
 * Nunca confiamos en un negocioId enviado desde
 * el frontend.
 *
 * ======================================================
 */

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin("*")
public class InventarioController {


    // =====================================================
    // 🔗 SERVICE
    // =====================================================

    private final InventarioService service;


    // =====================================================
    // 🏗 CONSTRUCTOR
    // =====================================================

    public InventarioController(
            InventarioService service
    ) {

        this.service =
                service;
    }


    // =====================================================
    // 📋 LISTAR INVENTARIO
    // ADMIN + EMPLEADO
    // =====================================================

    /**
     * Obtiene únicamente el inventario del negocio
     * autenticado.
     *
     * GET /api/inventario
     */

    @GetMapping
    public ResponseEntity<?> listar(
            Authentication auth
    ) {

        try {

            // ---------------------------------------------
            // 🔐 Obtener negocio autenticado
            // ---------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(auth);


            // ---------------------------------------------
            // 📋 Consultar inventario
            // ---------------------------------------------

            List<Inventario> inventario =
                    service.listar(
                            negocioId
                    );


            return ResponseEntity.ok(
                    inventario
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // 🔍 BUSCAR INVENTARIO POR ID
    // ADMIN + EMPLEADO
    // =====================================================

    /**
     * Busca un inventario por ID, limitado al negocio
     * autenticado.
     *
     * GET /api/inventario/{id}
     */

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            // ---------------------------------------------
            // 🔐 Obtener negocio
            // ---------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(auth);


            // ---------------------------------------------
            // 🔍 Buscar inventario
            // ---------------------------------------------

            Inventario inventario =
                    service.buscarPorId(
                            id,
                            negocioId
                    );


            // ---------------------------------------------
            // ❌ No encontrado
            // ---------------------------------------------

            if (inventario == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            return ResponseEntity.ok(
                    inventario
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // 💾 GUARDAR INVENTARIO
    // SOLO ADMIN
    // =====================================================

    /**
     * Crea un registro de inventario.
     *
     * POST /api/inventario
     *
     * El producto debe pertenecer al negocio autenticado.
     */

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> guardar(
            @RequestBody Inventario inventario,
            Authentication auth
    ) {

        try {

            // ---------------------------------------------
            // 🔐 Obtener negocio
            // ---------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(auth);


            // ---------------------------------------------
            // 💾 Guardar
            // ---------------------------------------------

            Inventario nuevo =
                    service.guardar(
                            inventario,
                            negocioId
                    );


            return ResponseEntity.ok(
                    nuevo
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // ✏ ACTUALIZAR INVENTARIO
    // SOLO ADMIN
    // =====================================================

    /**
     * Actualiza un registro de inventario.
     *
     * PUT /api/inventario/{id}
     */

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestBody Inventario inventario,
            Authentication auth
    ) {

        try {

            // ---------------------------------------------
            // 🔐 Obtener negocio
            // ---------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(auth);


            // ---------------------------------------------
            // ✏ Actualizar
            // ---------------------------------------------

            Inventario actualizado =
                    service.actualizar(
                            id,
                            inventario,
                            negocioId
                    );


            // ---------------------------------------------
            // ❌ No encontrado
            // ---------------------------------------------

            if (actualizado == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            return ResponseEntity.ok(
                    actualizado
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // ❌ ELIMINAR INVENTARIO
    // SOLO ADMIN
    // =====================================================

    /**
     * Elimina un registro de inventario.
     *
     * DELETE /api/inventario/{id}
     *
     * El Service verifica primero que el registro
     * pertenezca al negocio autenticado.
     */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            // ---------------------------------------------
            // 🔐 Obtener negocio
            // ---------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(auth);


            // ---------------------------------------------
            // ❌ Eliminar
            // ---------------------------------------------

            service.eliminar(
                    id,
                    negocioId
            );


            return ResponseEntity
                    .noContent()
                    .build();

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // 📉 STOCK BAJO
    // ADMIN + EMPLEADO
    // =====================================================

    /**
     * Obtiene los productos cuyo stock actual es menor
     * o igual al stock mínimo.
     *
     * GET /api/inventario/stock-bajo
     *
     * 🔐 La consulta está limitada al negocio autenticado.
     */

    @GetMapping("/stock-bajo")
    public ResponseEntity<?> stockBajo(
            Authentication auth
    ) {

        try {

            // ---------------------------------------------
            // 🔐 Obtener negocio
            // ---------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(auth);


            // ---------------------------------------------
            // 📉 Consultar stock bajo
            // ---------------------------------------------

            List<Inventario> inventario =
                    service.stockBajo(
                            negocioId
                    );


            return ResponseEntity.ok(
                    inventario
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // ➕ AUMENTAR STOCK
    // SOLO ADMIN
    // =====================================================

    /**
     * Aumenta el stock de un producto.
     *
     * POST /api/inventario/aumentar-stock
     *
     * Ejemplo de JSON:
     *
     * {
     *     "productoId": 5,
     *     "cantidad": 10
     * }
     *
     * 🔐 El negocio NO se recibe desde el frontend.
     *
     * Se obtiene del usuario autenticado.
     */

    @PostMapping("/aumentar-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> aumentarStock(
            @RequestBody Map<String, Object> datos,
            Authentication auth
    ) {

        try {

            // ---------------------------------------------
            // 🔐 Obtener negocio
            // ---------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(auth);


            // ---------------------------------------------
            // 📦 Obtener productoId
            // ---------------------------------------------

            Integer productoId =
                    obtenerInteger(
                            datos.get("productoId")
                    );


            // ---------------------------------------------
            // 📦 Obtener cantidad
            // ---------------------------------------------

            Integer cantidad =
                    obtenerInteger(
                            datos.get("cantidad")
                    );


            // ---------------------------------------------
            // ➕ Aumentar stock
            // ---------------------------------------------

            service.aumentarStock(
                    productoId,
                    cantidad,
                    negocioId
            );


            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Stock actualizado correctamente."
                    )
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =====================================================
    // 🔐 OBTENER NEGOCIO DEL USUARIO
    // =====================================================

    /**
     * Obtiene el negocio asociado al usuario autenticado.
     *
     * Nunca se utiliza un negocioId enviado por el frontend.
     */

    private Integer obtenerNegocioId(
            Authentication auth
    ) {

        // ---------------------------------------------
        // ❌ Validar autenticación
        // ---------------------------------------------

        if (
                auth == null ||
                        !(auth.getPrincipal()
                                instanceof CustomUserDetails user)
        ) {

            throw new IllegalStateException(
                    "Usuario no autenticado."
            );
        }


        // ---------------------------------------------
        // 🏪 Obtener negocio
        // ---------------------------------------------

        Integer negocioId =
                user.getNegocioId();


        // ---------------------------------------------
        // ❌ Usuario sin negocio
        // ---------------------------------------------

        if (negocioId == null) {

            throw new IllegalStateException(
                    "El usuario no tiene un negocio asociado."
            );
        }


        return negocioId;
    }


    // =====================================================
    // 🔢 CONVERTIR VALOR A INTEGER
    // =====================================================

    /**
     * Convierte valores recibidos desde JSON a Integer.
     *
     * Jackson puede entregar números como:
     *
     * Integer
     * Long
     * Double
     * String
     *
     * Por eso manejamos los casos principales.
     */

    private Integer obtenerInteger(
            Object valor
    ) {

        if (valor == null) {

            throw new IllegalArgumentException(
                    "Los datos enviados están incompletos."
            );
        }


        if (valor instanceof Integer numero) {

            return numero;
        }


        if (valor instanceof Number numero) {

            return numero.intValue();
        }


        if (valor instanceof String texto) {

            try {

                return Integer.valueOf(
                        texto
                );

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "El valor numérico no es válido."
                );
            }
        }


        throw new IllegalArgumentException(
                "El valor numérico no es válido."
        );
    }
}

