package com.ecomarket.controller;

import com.ecomarket.dto.RegistroDTO;
import com.ecomarket.model.Negocio;
import com.ecomarket.model.Usuario;
import com.ecomarket.repository.NegocioRepository;
import com.ecomarket.repository.UsuarioRepository;
import com.ecomarket.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class AuthController {

    // =========================================================
    // REPOSITORIO DE NEGOCIOS
    // =========================================================

    @Autowired
    private NegocioRepository negocioRepo;


    // =========================================================
    // REPOSITORIO DE USUARIOS
    // =========================================================

    @Autowired
    private UsuarioRepository usuarioRepo;


    // =========================================================
    // ENCODER DE CONTRASEÑAS
    // =========================================================

    @Autowired
    private PasswordEncoder passwordEncoder;


    // =========================================================
    // SERVICIO DE CORREO
    // =========================================================

    @Autowired
    private EmailService emailService;


    // =========================================================
    // VISTA DE REGISTRO
    // =========================================================

    @GetMapping("/registro")
    public String vistaRegistro() {

        return "registro";
    }


    // =========================================================
    // REGISTRAR NEGOCIO Y ADMINISTRADOR
    // =========================================================

    @PostMapping("/registro")
    @ResponseBody
    public String registrar(
            @RequestBody RegistroDTO dto
    ) {

        try {

            // =================================================
            // VALIDAR DATOS
            // =================================================

            if (dto == null) {

                return "DATOS_INVALIDOS";
            }


            // =================================================
            // VALIDAR NEGOCIO
            // =================================================

            if (dto.getNombreNegocio() == null ||
                    dto.getNombreNegocio().trim().isEmpty()) {

                return "NEGOCIO_REQUERIDO";
            }


            // =================================================
            // VALIDAR NOMBRE
            // =================================================

            if (dto.getNombre() == null ||
                    dto.getNombre().trim().isEmpty()) {

                return "NOMBRE_REQUERIDO";
            }


            // =================================================
            // VALIDAR EMAIL
            // =================================================

            if (dto.getEmail() == null ||
                    dto.getEmail().trim().isEmpty()) {

                return "EMAIL_REQUERIDO";
            }


            // =================================================
            // VALIDAR PASSWORD
            // =================================================

            if (dto.getPassword() == null ||
                    dto.getPassword().trim().isEmpty()) {

                return "PASSWORD_REQUERIDO";
            }


            // =================================================
            // NORMALIZAR EMAIL
            // =================================================

            String email =
                    dto.getEmail()
                            .trim()
                            .toLowerCase();


            // =================================================
            // COMPROBAR SI EL EMAIL EXISTE
            // =================================================

            if (usuarioRepo.findByEmail(email).isPresent()) {

                return "EMAIL_EXISTE";
            }


            // =================================================
            // CREAR NEGOCIO
            // =================================================

            Negocio negocio =
                    new Negocio();

            negocio.setNombre(
                    dto.getNombreNegocio()
                            .trim()
            );


            // =================================================
            // GUARDAR NEGOCIO
            // =================================================

            negocio =
                    negocioRepo.save(negocio);


            // =================================================
            // CREAR ADMIN
            // =================================================

            Usuario usuario =
                    new Usuario();


            usuario.setNombre(
                    dto.getNombre()
                            .trim()
            );


            usuario.setEmail(
                    email
            );


            // =================================================
            // CIFRAR PASSWORD
            // =================================================

            usuario.setPassword(
                    passwordEncoder.encode(
                            dto.getPassword()
                    )
            );


            // =================================================
            // ROL ADMIN
            // =================================================

            usuario.setRol(
                    "ADMIN"
            );


            // =================================================
            // ASOCIAR AL NEGOCIO
            // =================================================

            usuario.setNegocioId(
                    negocio.getId()
            );


            // =================================================
            // GUARDAR USUARIO
            // =================================================

            usuarioRepo.save(usuario);


            // =================================================
            // REGISTRO CORRECTO
            // =================================================

            return "REGISTRO_OK";


        } catch (Exception e) {

            // =================================================
            // MOSTRAR ERROR REAL EN CONSOLA
            // =================================================

            e.printStackTrace();


            // =================================================
            // RESPUESTA
            // =================================================

            return "ERROR_SERVIDOR";
        }
    }


    // =========================================================
    // VISTA RECUPERAR CONTRASEÑA
    // =========================================================

    @GetMapping("/recuperar")
    public String recuperarPassword() {

        return "recuperar";
    }


    // =========================================================
    // ENVIAR RECUPERACIÓN
    // =========================================================

    @PostMapping("/enviar-recuperacion")
    public String enviarRecuperacion(
            @RequestParam String email
    ) {

        Usuario usuario =
                usuarioRepo
                        .findByEmail(email)
                        .orElse(null);


        if (usuario == null) {

            return "redirect:/recuperar?error";
        }


        // =================================================
        // CREAR TOKEN
        // =================================================

        String token =
                UUID.randomUUID()
                        .toString();


        usuario.setToken(token);


        usuario.setTokenExpiracion(
                LocalDateTime.now()
                        .plusMinutes(15)
        );


        usuarioRepo.save(usuario);


        // =================================================
        // ENLACE
        // =================================================

        String enlace =
                "http://localhost:8080/reset-password?token="
                        + token;


        // =================================================
        // ENVIAR EMAIL
        // =================================================

        emailService.enviarRecuperacion(
                usuario.getEmail(),
                enlace
        );


        return "redirect:/recuperar?enviado";
    }


    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @GetMapping("/reset-password")
    public String resetPassword(
            @RequestParam String token
    ) {

        Usuario usuario =
                usuarioRepo
                        .findByToken(token)
                        .orElse(null);


        if (usuario == null) {

            return "redirect:/login?tokenInvalido";
        }


        if (usuario.getTokenExpiracion() == null ||
                usuario.getTokenExpiracion()
                        .isBefore(LocalDateTime.now())) {

            return "redirect:/login?tokenExpirado";
        }


        return "reset-password";
    }


    // =========================================================
    // GUARDAR NUEVA PASSWORD
    // =========================================================

    @PostMapping("/guardar-password")
    public String guardarPassword(
            @RequestParam String token,
            @RequestParam String password
    ) {

        Usuario usuario =
                usuarioRepo
                        .findByToken(token)
                        .orElse(null);


        if (usuario == null) {

            return "redirect:/login?tokenInvalido";
        }


        if (usuario.getTokenExpiracion() == null ||
                usuario.getTokenExpiracion()
                        .isBefore(LocalDateTime.now())) {

            return "redirect:/login?tokenExpirado";
        }


        // =================================================
        // CIFRAR PASSWORD
        // =================================================

        usuario.setPassword(
                passwordEncoder.encode(password)
        );


        // =================================================
        // ELIMINAR TOKEN
        // =================================================

        usuario.setToken(null);

        usuario.setTokenExpiracion(null);


        // =================================================
        // GUARDAR
        // =================================================

        usuarioRepo.save(usuario);


        return "redirect:/login?passwordActualizado";
    }

}