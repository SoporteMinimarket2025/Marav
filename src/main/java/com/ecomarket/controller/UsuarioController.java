package com.ecomarket.controller;

import com.ecomarket.model.Usuario;
import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * ============================================================
 * 👥 CONTROLADOR USUARIOS | ECOMARKET PRO
 * ============================================================
 *
 * ADMIN:
 *
 * - Crear
 * - Editar
 * - Activar / desactivar
 * - Eliminar
 *
 * EMPLEADO:
 *
 * - Consultar
 * - Buscar
 *
 * ============================================================
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin
public class UsuarioController {


    @Autowired
    private UsuarioService usuarioService;


    // ========================================================
    // 🔐 USUARIO AUTENTICADO
    // ========================================================

    private CustomUserDetails
    obtenerUsuarioLogueado() {


        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        return
                (CustomUserDetails)
                        authentication.getPrincipal();

    }


    // ========================================================
    // 🏢 NEGOCIO ACTUAL
    // ========================================================

    private Integer obtenerNegocioActual() {

        return obtenerUsuarioLogueado()
                .getNegocioId();

    }


    // ========================================================
    // 📋 MIS USUARIOS
    // ========================================================

    @GetMapping("/mios")
    public ResponseEntity<List<Usuario>>
    misUsuarios() {


        Integer negocioId =
                obtenerNegocioActual();


        return ResponseEntity.ok(
                usuarioService
                        .listarPorNegocio(
                                negocioId
                        )
        );

    }


    // ========================================================
    // ➕ CREAR
    // ========================================================

    @PostMapping
    public ResponseEntity<Usuario>
    guardar(
            @RequestBody Usuario usuario) {


        /*
         * IMPORTANTE:
         *
         * El frontend NO envía negocioId.
         *
         * El servidor lo obtiene del usuario
         * actualmente autenticado.
         */

        Integer negocioId =
                obtenerNegocioActual();


        usuario.setNegocioId(
                negocioId
        );


        Usuario nuevo =
                usuarioService.guardar(
                        usuario
                );


        return ResponseEntity.ok(
                nuevo
        );

    }


    // ========================================================
    // 🔎 CONSULTAR
    // ========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Usuario>
    buscar(
            @PathVariable Integer id) {


        Integer negocioId =
                obtenerNegocioActual();


        return usuarioService
                .buscarPorIdYNegocio(
                        id,
                        negocioId
                )
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );

    }


    // ========================================================
    // ✏️ ACTUALIZAR
    // ========================================================

    @PutMapping("/{id}")
    public ResponseEntity<Usuario>
    actualizar(
            @PathVariable Integer id,
            @RequestBody Usuario usuario) {


        Integer negocioId =
                obtenerNegocioActual();


        return ResponseEntity.ok(
                usuarioService.actualizar(
                        id,
                        negocioId,
                        usuario
                )
        );

    }


    // ========================================================
    // 🔄 ESTADO
    // ========================================================

    @PutMapping("/{id}/estado")
    public ResponseEntity<Usuario>
    cambiarEstado(
            @PathVariable Integer id) {


        Integer negocioId =
                obtenerNegocioActual();


        return ResponseEntity.ok(
                usuarioService.cambiarEstado(
                        id,
                        negocioId
                )
        );

    }


    // ========================================================
    // 🗑️ ELIMINAR
    // ========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    eliminar(
            @PathVariable Integer id) {


        Integer negocioId =
                obtenerNegocioActual();


        usuarioService.eliminar(
                id,
                negocioId
        );


        return ResponseEntity
                .noContent()
                .build();

    }


    // ========================================================
    // 🏢 CONSULTA POR NEGOCIO
    // ========================================================
    //
    // Se conserva para compatibilidad con otras partes
    // del proyecto.
    //
    // El módulo Usuarios utiliza /mios.
    //
    // ========================================================

    @GetMapping("/negocio/{negocioId}")
    public ResponseEntity<List<Usuario>>
    negocio(
            @PathVariable Integer negocioId) {


        return ResponseEntity.ok(
                usuarioService
                        .listarPorNegocio(
                                negocioId
                        )
        );

    }

}

