package com.ecomarket.service;

import com.ecomarket.model.Usuario;
import com.ecomarket.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * ============================================================
 * 👥 SERVICIO DE USUARIOS | ECOMARKET PRO
 * ============================================================
 *
 * Aquí se encuentra la lógica de negocio del módulo Usuarios.
 *
 * ============================================================
 */
@Service
public class UsuarioService {


    @Autowired
    private UsuarioRepository usuarioRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    // ========================================================
    // ➕ GUARDAR
    // ========================================================

    public Usuario guardar(
            Usuario usuario) {


        /*
         * La contraseña se cifra antes de almacenarla.
         */

        if (
                usuario.getPassword() != null &&
                        !usuario.getPassword()
                                .trim()
                                .isEmpty()
        ) {

            usuario.setPassword(
                    passwordEncoder.encode(
                            usuario.getPassword()
                    )
            );

        }


        return usuarioRepository.save(
                usuario
        );

    }


    // ========================================================
    // 📋 LISTAR TODOS
    // ========================================================

    public List<Usuario> listar() {

        return usuarioRepository.findAll();

    }


    // ========================================================
    // 🔎 BUSCAR POR ID
    // ========================================================

    public Optional<Usuario> buscarPorId(
            Integer id) {

        return usuarioRepository.findById(
                id
        );

    }


    // ========================================================
    // 🔎 BUSCAR POR ID + NEGOCIO
    // ========================================================

    public Optional<Usuario> buscarPorIdYNegocio(
            Integer id,
            Integer negocioId) {

        return usuarioRepository
                .findByIdAndNegocioId(
                        id,
                        negocioId
                );

    }


    // ========================================================
    // 🏢 LISTAR POR NEGOCIO
    // ========================================================

    public List<Usuario> listarPorNegocio(
            Integer negocioId) {

        return usuarioRepository
                .findByNegocioId(
                        negocioId
                );

    }


    // ========================================================
    // 📧 BUSCAR EMAIL
    // ========================================================

    public Optional<Usuario> buscarPorEmail(
            String email) {

        return usuarioRepository
                .findByEmail(
                        email
                );

    }


    // ========================================================
    // 🔑 BUSCAR TOKEN
    // ========================================================

    public Optional<Usuario> buscarPorToken(
            String token) {

        return usuarioRepository
                .findByToken(
                        token
                );

    }


    // ========================================================
    // ✏️ ACTUALIZAR
    // ========================================================

    public Usuario actualizar(
            Integer id,
            Integer negocioId,
            Usuario datos) {


        Usuario usuario =
                usuarioRepository
                        .findByIdAndNegocioId(
                                id,
                                negocioId
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Usuario no encontrado."
                                )
                        );


        // ----------------------------------------------------
        // INFORMACIÓN PERSONAL
        // ----------------------------------------------------

        usuario.setNombre(
                datos.getNombre()
        );

        usuario.setTipoIdentificacion(
                datos.getTipoIdentificacion()
        );

        usuario.setNumeroIdentificacion(
                datos.getNumeroIdentificacion()
        );

        usuario.setEmail(
                datos.getEmail()
        );


        // ----------------------------------------------------
        // ROL
        // ----------------------------------------------------

        usuario.setRol(
                datos.getRol()
        );


        // ----------------------------------------------------
        // ESTADO
        // ----------------------------------------------------

        usuario.setEstado(
                datos.getEstado()
        );


        // ----------------------------------------------------
        // CONTRASEÑA
        // ----------------------------------------------------
        //
        // Si llega vacía:
        // conserva la actual.
        //
        // Si llega una nueva:
        // la cifra y reemplaza.
        //
        // ----------------------------------------------------

        if (
                datos.getPassword() != null &&
                        !datos.getPassword()
                                .trim()
                                .isEmpty()
        ) {

            usuario.setPassword(
                    passwordEncoder.encode(
                            datos.getPassword()
                    )
            );

        }


        return usuarioRepository.save(
                usuario
        );

    }


    // ========================================================
    // 🔄 CAMBIAR ESTADO
    // ========================================================

    public Usuario cambiarEstado(
            Integer id,
            Integer negocioId) {


        Usuario usuario =
                usuarioRepository
                        .findByIdAndNegocioId(
                                id,
                                negocioId
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Usuario no encontrado."
                                )
                        );


        Boolean estado =
                usuario.getEstado();


        if (estado == null) {

            estado = true;

        }


        usuario.setEstado(
                !estado
        );


        return usuarioRepository.save(
                usuario
        );

    }


    // ========================================================
    // 🗑️ ELIMINAR
    // ========================================================

    public void eliminar(
            Integer id,
            Integer negocioId) {


        Usuario usuario =
                usuarioRepository
                        .findByIdAndNegocioId(
                                id,
                                negocioId
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Usuario no encontrado."
                                )
                        );


        usuarioRepository.delete(
                usuario
        );

    }

}

