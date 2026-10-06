// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.controller;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Producto;
import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.ProductoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ======================================================
// 🚀 CONTROLADOR REST PRODUCTOS (PRO)
// ======================================================

/**
 * ======================================================
 * 📦 API PRODUCTOS
 * ======================================================
 *
 * ✔ Listar productos
 * ✔ Buscar productos
 * ✔ Crear productos
 * ✔ Consultar producto por ID
 * ✔ Editar productos
 * ✔ Eliminar productos
 * ✔ Aislamiento por negocio
 *
 * ======================================================
 *
 * 🔐 REGLA MULTI-NEGOCIO
 *
 * El negocioId NUNCA debe venir confiado desde el
 * frontend.
 *
 * Se obtiene de:
 *
 * Authentication
 *       ↓
 * CustomUserDetails
 *       ↓
 * negocioId
 *
 * Esto permite garantizar que cada negocio solamente
 * pueda trabajar con sus propios productos.
 *
 * ======================================================
 */

@RestController
@RequestMapping("/api/productos")
@CrossOrigin
public class ProductoController {


    // ==================================================
    // 🔗 SERVICE
    // ==================================================

    @Autowired
    private ProductoService service;


    // ==================================================
    // 📋 LISTAR PRODUCTOS
    // ==================================================

    /**
     * Obtiene únicamente los productos activos del
     * negocio del usuario autenticado.
     *
     * GET /api/productos
     */

    @GetMapping
    public ResponseEntity<List<Producto>> listar(
            Authentication auth
    ) {

        // ----------------------------------------------
        // 🔐 Obtener usuario autenticado
        // ----------------------------------------------

        CustomUserDetails user =
                obtenerUsuario(auth);


        // ----------------------------------------------
        // 🏪 Obtener negocio
        // ----------------------------------------------

        Integer negocioId =
                obtenerNegocioId(user);


        // ----------------------------------------------
        // 📦 Consultar productos del negocio
        // ----------------------------------------------

        List<Producto> productos =
                service.listarPorNegocio(
                        negocioId
                );


        return ResponseEntity.ok(
                productos
        );
    }


    // ==================================================
    // 🔍 BUSCAR PRODUCTOS POR NOMBRE
    // ==================================================

    /**
     * Busca productos por nombre.
     *
     * Ejemplo:
     *
     * GET /api/productos/buscar?nombre=arroz
     *
     * La búsqueda también queda limitada al negocio
     * autenticado.
     */

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNombre(
            @RequestParam(required = false) String nombre,
            Authentication auth
    ) {

        try {

            // ------------------------------------------
            // 🔐 Usuario autenticado
            // ------------------------------------------

            CustomUserDetails user =
                    obtenerUsuario(auth);


            // ------------------------------------------
            // 🏪 Negocio autenticado
            // ------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(user);


            // ------------------------------------------
            // 🔎 Buscar productos
            // ------------------------------------------

            List<Producto> productos =
                    service.buscarPorNombre(
                            negocioId,
                            nombre
                    );


            return ResponseEntity.ok(
                    productos
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==================================================
    // 💾 GUARDAR PRODUCTO
    // ==================================================

    /**
     * Crea un nuevo producto.
     *
     * POST /api/productos
     *
     * 🔐 El negocio se obtiene del usuario autenticado.
     */

    @PostMapping
    public ResponseEntity<?> guardar(
            @RequestBody Producto producto,
            Authentication auth
    ) {

        try {

            // ------------------------------------------
            // 🔐 Usuario autenticado
            // ------------------------------------------

            CustomUserDetails user =
                    obtenerUsuario(auth);


            // ------------------------------------------
            // 🏪 Obtener negocio real
            // ------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(user);


            // ------------------------------------------
            // 🔐 GUARDAR
            // ------------------------------------------
            //
            // El negocioId viene directamente del usuario
            // autenticado.
            //
            // No dependemos del negocioId enviado por
            // el frontend.
            //
            // ------------------------------------------

            Producto nuevo =
                    service.guardar(
                            producto,
                            negocioId
                    );


            return ResponseEntity.ok(
                    nuevo
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==================================================
    // 🔍 BUSCAR PRODUCTO POR ID
    // ==================================================

    /**
     * Busca un producto por ID.
     *
     * GET /api/productos/{id}
     *
     * 🔐 La búsqueda se hace por:
     *
     * ID + negocioId
     *
     * De esta manera un negocio no puede consultar
     * productos pertenecientes a otro negocio.
     */

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            // ------------------------------------------
            // 🔐 Usuario autenticado
            // ------------------------------------------

            CustomUserDetails user =
                    obtenerUsuario(auth);


            // ------------------------------------------
            // 🏪 Negocio autenticado
            // ------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(user);


            // ------------------------------------------
            // 🔐 Buscar ID + negocio
            // ------------------------------------------

            Producto producto =
                    service.buscarPorId(
                            id,
                            negocioId
                    );


            // ------------------------------------------
            // ❌ No encontrado
            // ------------------------------------------

            if (producto == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            return ResponseEntity.ok(
                    producto
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==================================================
    // ✏ ACTUALIZAR PRODUCTO
    // ==================================================

    /**
     * Actualiza un producto.
     *
     * PUT /api/productos/{id}
     *
     * 🔐 El producto solamente puede actualizarse si
     * pertenece al negocio autenticado.
     */

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestBody Producto producto,
            Authentication auth
    ) {

        try {

            // ------------------------------------------
            // 🔐 Usuario autenticado
            // ------------------------------------------

            CustomUserDetails user =
                    obtenerUsuario(auth);


            // ------------------------------------------
            // 🏪 Negocio autenticado
            // ------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(user);


            // ------------------------------------------
            // ✏ Actualizar
            // ------------------------------------------

            Producto actualizado =
                    service.actualizar(
                            id,
                            producto,
                            negocioId
                    );


            // ------------------------------------------
            // ❌ No existe o pertenece a otro negocio
            // ------------------------------------------

            if (actualizado == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            return ResponseEntity.ok(
                    actualizado
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==================================================
    // ❌ ELIMINAR PRODUCTO
    // ==================================================

    /**
     * Realiza una eliminación lógica.
     *
     * DELETE /api/productos/{id}
     *
     * El producto no se elimina físicamente.
     *
     * Se cambia:
     *
     * estado = false
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            // ------------------------------------------
            // 🔐 Usuario autenticado
            // ------------------------------------------

            CustomUserDetails user =
                    obtenerUsuario(auth);


            // ------------------------------------------
            // 🏪 Negocio autenticado
            // ------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(user);


            // ------------------------------------------
            // ❌ Eliminar únicamente dentro del negocio
            // ------------------------------------------

            boolean eliminado =
                    service.eliminar(
                            id,
                            negocioId
                    );


            // ------------------------------------------
            // ❌ Producto no encontrado
            // ------------------------------------------

            if (!eliminado) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            // ------------------------------------------
            // ✅ Eliminación correcta
            // ------------------------------------------

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==================================================
    // 🔐 OBTENER USUARIO AUTENTICADO
    // ==================================================

    /**
     * Obtiene el CustomUserDetails desde Spring Security.
     */

    private CustomUserDetails obtenerUsuario(
            Authentication auth
    ) {

        // ----------------------------------------------
        // ❌ Validar autenticación
        // ----------------------------------------------

        if (auth == null ||
                !(auth.getPrincipal()
                        instanceof CustomUserDetails user)) {

            throw new IllegalStateException(
                    "Usuario no autenticado."
            );
        }


        return user;
    }


    // ==================================================
    // 🏪 OBTENER NEGOCIO DEL USUARIO
    // ==================================================

    /**
     * Obtiene el negocio asociado al usuario autenticado.
     *
     * Nunca se recibe desde el frontend.
     */

    private Integer obtenerNegocioId(
            CustomUserDetails user
    ) {

        Integer negocioId =
                user.getNegocioId();


        // ----------------------------------------------
        // ❌ Usuario sin negocio
        // ----------------------------------------------

        if (negocioId == null) {

            throw new IllegalStateException(
                    "El usuario no tiene un negocio asociado."
            );
        }


        return negocioId;
    }
}

