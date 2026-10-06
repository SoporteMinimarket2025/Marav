package com.ecomarket.repository;

import com.ecomarket.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/**
 * ============================================================
 * 👥 REPOSITORIO DE USUARIOS | ECOMARKET PRO
 * ============================================================
 *
 * Acceso a la tabla:
 *
 * usuarios
 *
 * ============================================================
 */
public interface UsuarioRepository
        extends JpaRepository<Usuario, Integer> {


    // ========================================================
    // 🏢 USUARIOS DEL NEGOCIO
    // ========================================================

    List<Usuario> findByNegocioId(
            Integer negocioId
    );


    // ========================================================
    // 🔎 USUARIO POR ID Y NEGOCIO
    // ========================================================
    //
    // Se utiliza para evitar que un usuario de un negocio
    // pueda modificar o eliminar usuarios de otro negocio.
    //
    // ========================================================

    Optional<Usuario> findByIdAndNegocioId(
            Integer id,
            Integer negocioId
    );


    // ========================================================
    // 📧 BUSCAR POR EMAIL
    // ========================================================

    Optional<Usuario> findByEmail(
            String email
    );


    // ========================================================
    // 🔑 BUSCAR POR TOKEN
    // ========================================================

    Optional<Usuario> findByToken(
            String token
    );

}

