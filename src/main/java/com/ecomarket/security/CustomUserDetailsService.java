// ======================================================
// 📁 PACKAGE
// ======================================================

package com.ecomarket.security;



// ======================================================
// 📚 IMPORTS
// ======================================================

import com.ecomarket.model.Usuario;

import com.ecomarket.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;





// ======================================================
// 🔐 SERVICIO DE AUTENTICACIÓN
// ======================================================
//
// Spring Security utiliza esta clase para:
//
// ✔ Buscar usuarios en MySQL
// ✔ Validar correo
// ✔ Verificar si el usuario está activo
// ✔ Cargar roles/permisos
//
// ======================================================


@Service
public class CustomUserDetailsService
        implements UserDetailsService {





    // ==================================================
    // 📦 REPOSITORIO USUARIO
    // ==================================================
    //
    // Permite consultar la tabla usuario
    //
    // ==================================================


    private final UsuarioRepository usuarioRepository;








    // ==================================================
    // 🔥 CONSTRUCTOR
    // ==================================================
    //
    // Inyección de dependencia recomendada
    //
    // ==================================================


    public CustomUserDetailsService(
            UsuarioRepository usuarioRepository
    ) {

        this.usuarioRepository = usuarioRepository;

    }









    // ==================================================
    // 🔍 BUSCAR USUARIO POR EMAIL
    // ==================================================
    //
    // Spring Security llama este método cuando:
    //
    // Usuario escribe:
    //
    // correo
    // contraseña
    //
    // en login.html
    //
    // ==================================================


    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {




        // ==============================================
        // 🔎 CONSULTAR USUARIO EN BD
        // ==============================================


        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(() ->

                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        )

                );







        // ==============================================
        // 🚫 VALIDAR ESTADO DEL USUARIO
        // ==============================================
        //
        // Si el usuario está inactivo:
        //
        // estado = false
        //
        // No podrá ingresar
        //
        // ==============================================


        if(usuario.getEstado() != null
                && !usuario.getEstado()) {


            throw new UsernameNotFoundException(
                    "Usuario deshabilitado"
            );


        }








        // ==============================================
        // 🔐 DEVOLVER USUARIO A SPRING SECURITY
        // ==============================================
        //
        // Aquí se cargan:
        //
        // ✔ Email
        // ✔ Password encriptada
        // ✔ Rol ADMIN / EMPLEADO
        //
        // ==============================================


        return new CustomUserDetails(usuario);



    }



}

