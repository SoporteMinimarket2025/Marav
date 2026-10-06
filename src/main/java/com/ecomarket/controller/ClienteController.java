
// ======================================================
// 📁 PACKAGE
// ======================================================
        package com.ecomarket.controller;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.dto.ClienteDTO;
import com.ecomarket.model.Cliente;
import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.ClienteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ======================================================
 * 👥 API CLIENTES
 * ======================================================
 *
 * ✔ Listar clientes
 * ✔ Buscar cliente
 * ✔ Crear cliente
 * ✔ Editar cliente
 * ✔ Eliminar lógico
 * ✔ Preparado para POS
 * ✔ Compatible con ventas fiadas
 * ✔ Aislamiento por negocio
 *
 * ======================================================
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin
public class ClienteController {

    // ==================================================
    // 🔗 SERVICE
    // ==================================================
    @Autowired
    private ClienteService service;


    // ==================================================
    // 📋 LISTAR CLIENTES
    // ==================================================
    @GetMapping
    public ResponseEntity<List<Cliente>> listar(
            Authentication auth
    ) {

        Integer negocioId =
                obtenerNegocioId(auth);

        return ResponseEntity.ok(
                service.listar(negocioId)
        );
    }


    // ==================================================
    // 🔍 BUSCAR CLIENTES POR NOMBRE
    // ==================================================
    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscar(
            @RequestParam String nombre,
            Authentication auth
    ) {

        Integer negocioId =
                obtenerNegocioId(auth);

        return ResponseEntity.ok(
                service.buscar(
                        negocioId,
                        nombre
                )
        );
    }


    // ==================================================
    // 💾 GUARDAR CLIENTE
    // ==================================================
    @PostMapping
    public ResponseEntity<?> guardar(
            @RequestBody ClienteDTO dto,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            Cliente cliente =
                    service.guardar(
                            dto,
                            negocioId
                    );

            return ResponseEntity.ok(
                    cliente
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==================================================
    // 🔍 BUSCAR CLIENTE POR ID
    // ==================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            Cliente cliente =
                    service.buscarPorId(
                            id,
                            negocioId
                    );

            // =================================================
            // ❌ NO EXISTE O NO PERTENECE AL NEGOCIO
            // =================================================
            if (cliente == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            return ResponseEntity.ok(
                    cliente
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==================================================
    // ✏ ACTUALIZAR CLIENTE
    // ==================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestBody ClienteDTO dto,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            Cliente cliente =
                    service.actualizar(
                            id,
                            dto,
                            negocioId
                    );

            // =================================================
            // ❌ NO EXISTE O NO PERTENECE AL NEGOCIO
            // =================================================
            if (cliente == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            return ResponseEntity.ok(
                    cliente
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==================================================
    // ❌ ELIMINAR CLIENTE
    // ==================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id,
            Authentication auth
    ) {

        try {

            Integer negocioId =
                    obtenerNegocioId(auth);

            service.eliminar(
                    id,
                    negocioId
            );

            return ResponseEntity.ok(
                    "Cliente eliminado correctamente"
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // ==================================================
    // 🔐 OBTENER NEGOCIO DEL USUARIO AUTENTICADO
    // ==================================================
    private Integer obtenerNegocioId(
            Authentication auth
    ) {

        if (auth == null
                || !(auth.getPrincipal()
                instanceof CustomUserDetails user)) {

            throw new IllegalStateException(
                    "Usuario no autenticado."
            );
        }

        Integer negocioId =
                user.getNegocioId();

        if (negocioId == null) {

            throw new IllegalStateException(
                    "El usuario no tiene un negocio asociado."
            );
        }

        return negocioId;
    }
}

