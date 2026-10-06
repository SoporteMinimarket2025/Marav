package com.ecomarket.controller;

import com.ecomarket.model.Proveedor;
import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.ProveedorService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ============================================================
 * CONTROLADOR DE PROVEEDORES
 * ============================================================
 *
 * Se encarga de:
 *
 * - Mostrar la vista de proveedores.
 * - Listar proveedores.
 * - Crear proveedores.
 * - Actualizar proveedores.
 * - Eliminar proveedores.
 *
 * IMPORTANTE:
 *
 * EcoMarket PRO es multi-negocio.
 *
 * Por eso el negocioId SIEMPRE se obtiene desde:
 *
 *      CustomUserDetails
 *
 * y NO desde el frontend.
 *
 * Esto garantiza que cada negocio trabaje únicamente
 * con sus propios proveedores.
 * ============================================================
 */
@Controller
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {


    // =========================================================
    // SERVICIO
    // =========================================================

    private final ProveedorService proveedorService;


    // =========================================================
    // VISTA PRINCIPAL
    // =========================================================

    /**
     * Carga la página de proveedores.
     */
    @GetMapping
    public String listar(
            Model model,
            Authentication authentication
    ) {

        // -----------------------------------------------------
        // OBTENER NEGOCIO DEL USUARIO AUTENTICADO
        // -----------------------------------------------------

        Integer negocioId =
                obtenerNegocioId(authentication);


        // -----------------------------------------------------
        // MENÚ ACTIVO
        // -----------------------------------------------------

        model.addAttribute(
                "menuActivo",
                "proveedores"
        );


        // -----------------------------------------------------
        // LISTAR SOLO PROVEEDORES DEL NEGOCIO
        // -----------------------------------------------------

        model.addAttribute(
                "proveedores",
                proveedorService.listar(
                        negocioId
                )
        );


        // -----------------------------------------------------
        // PROVEEDOR NUEVO
        // -----------------------------------------------------

        model.addAttribute(
                "proveedor",
                new Proveedor()
        );


        // -----------------------------------------------------
        // DATOS DEL USUARIO
        // -----------------------------------------------------

        cargarDatosUsuario(
                model,
                authentication
        );


        return "proveedores";
    }


    // =========================================================
    // API LISTAR
    // =========================================================

    /**
     * Devuelve los proveedores del negocio autenticado.
     *
     * GET:
     *
     * /proveedores/api
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Proveedor>> listarApi(
            Authentication authentication
    ) {

        Integer negocioId =
                obtenerNegocioId(authentication);


        return ResponseEntity.ok(
                proveedorService.listar(
                        negocioId
                )
        );
    }


    // =========================================================
    // API BUSCAR
    // =========================================================

    /**
     * Busca proveedores por nombre.
     *
     * GET:
     *
     * /proveedores/api/buscar?nombre=...
     *
     * La búsqueda también está limitada al negocio
     * del usuario autenticado.
     */
    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<List<Proveedor>> buscarApi(
            @RequestParam(
                    name = "nombre",
                    required = false,
                    defaultValue = ""
            )
            String nombre,

            Authentication authentication
    ) {

        Integer negocioId =
                obtenerNegocioId(authentication);


        return ResponseEntity.ok(
                proveedorService.buscar(
                        nombre,
                        negocioId
                )
        );
    }


    // =========================================================
    // API BUSCAR POR ID
    // =========================================================

    /**
     * Obtiene un proveedor por ID.
     *
     * Solo será encontrado si pertenece al negocio
     * del usuario autenticado.
     *
     * GET:
     *
     * /proveedores/api/5
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Proveedor> buscarPorIdApi(
            @PathVariable Integer id,
            Authentication authentication
    ) {

        Integer negocioId =
                obtenerNegocioId(authentication);


        Proveedor proveedor =
                proveedorService.buscarPorId(
                        id,
                        negocioId
                );


        // -----------------------------------------------------
        // PROVEEDOR NO ENCONTRADO
        // -----------------------------------------------------

        if (proveedor == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }


        return ResponseEntity.ok(
                proveedor
        );
    }


    // =========================================================
    // API GUARDAR
    // =========================================================

    /**
     * Crea un nuevo proveedor.
     *
     * POST:
     *
     * /proveedores/api
     *
     * El negocioId enviado por el frontend, si existiera,
     * NO se utiliza.
     *
     * El negocio se asigna desde CustomUserDetails.
     */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Proveedor> guardarApi(
            @RequestBody Proveedor proveedor,
            Authentication authentication
    ) {

        Integer negocioId =
                obtenerNegocioId(authentication);


        /*
         * El servicio asignará negocioId utilizando
         * el valor obtenido del usuario autenticado.
         */
        Proveedor guardado =
                proveedorService.guardar(
                        proveedor,
                        negocioId
                );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guardado);
    }


    // =========================================================
    // API ACTUALIZAR
    // =========================================================

    /**
     * Actualiza un proveedor existente.
     *
     * PUT:
     *
     * /proveedores/api/{id}
     *
     * IMPORTANTE:
     *
     * No utilizamos guardar() directamente.
     *
     * Primero verificamos que el proveedor pertenezca
     * al negocio autenticado.
     */
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Proveedor> actualizarApi(
            @PathVariable Integer id,
            @RequestBody Proveedor datos,
            Authentication authentication
    ) {

        Integer negocioId =
                obtenerNegocioId(authentication);


        // -----------------------------------------------------
        // BUSCAR PROVEEDOR DENTRO DEL NEGOCIO
        // -----------------------------------------------------

        Proveedor proveedor =
                proveedorService.buscarPorId(
                        id,
                        negocioId
                );


        // -----------------------------------------------------
        // NO EXISTE O NO PERTENECE AL NEGOCIO
        // -----------------------------------------------------

        if (proveedor == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }


        // -----------------------------------------------------
        // ACTUALIZAR CAMPOS
        // -----------------------------------------------------

        if (datos.getNombre() != null) {
            proveedor.setNombre(
                    datos.getNombre()
            );
        }

        proveedor.setNit(
                datos.getNit()
        );

        proveedor.setTelefono(
                datos.getTelefono()
        );

        proveedor.setEmail(
                datos.getEmail()
        );

        proveedor.setDireccion(
                datos.getDireccion()
        );

        proveedor.setContacto(
                datos.getContacto()
        );

        proveedor.setEstado(
                datos.getEstado()
        );


        /*
         * IMPORTANTE:
         *
         * NO hacemos:
         *
         * proveedor.setNegocioId(
         *     datos.getNegocioId()
         * );
         *
         * El negocio original se conserva.
         */


        // -----------------------------------------------------
        // GUARDAR CAMBIOS
        // -----------------------------------------------------

        Proveedor actualizado =
                proveedorService.guardar(
                        proveedor,
                        negocioId
                );


        return ResponseEntity.ok(
                actualizado
        );
    }


    // =========================================================
    // API ELIMINAR
    // =========================================================

    /**
     * Elimina un proveedor.
     *
     * DELETE:
     *
     * /proveedores/api/{id}
     *
     * El servicio verifica previamente que el proveedor
     * pertenezca al negocio autenticado.
     */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarApi(
            @PathVariable Integer id,
            Authentication authentication
    ) {

        Integer negocioId =
                obtenerNegocioId(authentication);


        proveedorService.eliminar(
                id,
                negocioId
        );


        return ResponseEntity.noContent()
                .build();
    }


    // =========================================================
    // DATOS DEL USUARIO PARA THYMELEAF
    // =========================================================

    /**
     * Carga nombre, rol y negocio del usuario
     * para utilizarlos en la interfaz.
     */
    private void cargarDatosUsuario(
            Model model,
            Authentication authentication
    ) {

        // -----------------------------------------------------
        // VALIDAR AUTENTICACIÓN
        // -----------------------------------------------------

        if (authentication == null
                || !authentication.isAuthenticated()) {

            model.addAttribute(
                    "usuario",
                    "Usuario"
            );

            model.addAttribute(
                    "rolUsuario",
                    "USUARIO"
            );

            return;
        }


        // -----------------------------------------------------
        // OBTENER PRINCIPAL
        // -----------------------------------------------------

        Object principal =
                authentication.getPrincipal();


        // =====================================================
        // CUSTOM USER DETAILS
        // =====================================================

        if (principal instanceof CustomUserDetails user) {

            // -------------------------------------------------
            // NOMBRE
            // -------------------------------------------------

            model.addAttribute(
                    "usuario",
                    user.getNombre()
            );


            // -------------------------------------------------
            // ROL
            // -------------------------------------------------

            String rol =
                    authentication
                            .getAuthorities()
                            .stream()
                            .map(
                                    GrantedAuthority::getAuthority
                            )
                            .findFirst()
                            .orElse("USUARIO");


            // -------------------------------------------------
            // QUITAR ROLE_
            // -------------------------------------------------

            if (rol.startsWith("ROLE_")) {

                rol = rol.substring(5);
            }


            // -------------------------------------------------
            // GUARDAR ROL
            // -------------------------------------------------

            model.addAttribute(
                    "rolUsuario",
                    rol
            );


            // -------------------------------------------------
            // NEGOCIO
            // -------------------------------------------------

            model.addAttribute(
                    "negocioId",
                    user.getNegocioId()
            );

            return;
        }


        // =====================================================
        // RESPALDO
        // =====================================================

        model.addAttribute(
                "usuario",
                authentication.getName()
        );


        String rol =
                authentication
                        .getAuthorities()
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
                        .findFirst()
                        .orElse("USUARIO");


        if (rol.startsWith("ROLE_")) {

            rol = rol.substring(5);
        }


        model.addAttribute(
                "rolUsuario",
                rol
        );
    }


    // =========================================================
    // OBTENER NEGOCIO DEL USUARIO AUTENTICADO
    // =========================================================

    /**
     * Obtiene el negocio directamente desde
     * CustomUserDetails.
     *
     * Nunca se toma negocioId desde:
     *
     * - RequestParam
     * - RequestBody
     * - PathVariable
     * - JavaScript
     *
     * Esto es fundamental para la seguridad multi-negocio.
     */
    private Integer obtenerNegocioId(
            Authentication authentication
    ) {

        // -----------------------------------------------------
        // VALIDAR AUTENTICACIÓN
        // -----------------------------------------------------

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "El usuario no está autenticado."
            );
        }


        // -----------------------------------------------------
        // OBTENER PRINCIPAL
        // -----------------------------------------------------

        Object principal =
                authentication.getPrincipal();


        // -----------------------------------------------------
        // VALIDAR CUSTOM USER DETAILS
        // -----------------------------------------------------

        if (!(principal instanceof CustomUserDetails user)) {

            throw new IllegalStateException(
                    "No fue posible obtener los datos "
                            + "del usuario autenticado."
            );
        }


        // -----------------------------------------------------
        // OBTENER NEGOCIO
        // -----------------------------------------------------

        Integer negocioId =
                user.getNegocioId();


        // -----------------------------------------------------
        // VALIDAR NEGOCIO
        // -----------------------------------------------------

        if (negocioId == null) {

            throw new IllegalStateException(
                    "El usuario autenticado "
                            + "no tiene un negocio asociado."
            );
        }


        return negocioId;
    }
}

