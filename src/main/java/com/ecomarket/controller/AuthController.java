
        package com.ecomarket.controller;

import com.ecomarket.dto.RegistroDTO;
import com.ecomarket.model.Negocio;
import com.ecomarket.model.Usuario;
import com.ecomarket.repository.NegocioRepository;
import com.ecomarket.repository.UsuarioRepository;
import com.ecomarket.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class AuthController {

    // =========================================================
    // REPOSITORIOS Y SERVICIOS
    // =========================================================

    @Autowired
    private NegocioRepository negocioRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    // =========================================================
    // URL BASE PÚBLICA DE MARAV
    // En Railway se configura mediante APP_BASE_URL.
    // =========================================================

    @Value("${APP_BASE_URL:http://localhost:8080}")
    private String appBaseUrl;

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
    public String registrar(@RequestBody RegistroDTO dto) {

        try {
            if (dto == null) {
                return "DATOS_INVALIDOS";
            }

            if (dto.getNombreNegocio() == null
                    || dto.getNombreNegocio().trim().isEmpty()) {
                return "NEGOCIO_REQUERIDO";
            }

            if (dto.getNombre() == null
                    || dto.getNombre().trim().isEmpty()) {
                return "NOMBRE_REQUERIDO";
            }

            if (dto.getEmail() == null
                    || dto.getEmail().trim().isEmpty()) {
                return "EMAIL_REQUERIDO";
            }

            if (dto.getPassword() == null
                    || dto.getPassword().trim().isEmpty()) {
                return "PASSWORD_REQUERIDO";
            }

            String email = dto.getEmail()
                    .trim()
                    .toLowerCase();

            if (usuarioRepo.findByEmail(email).isPresent()) {
                return "EMAIL_EXISTE";
            }

            // Crear y guardar el negocio.
            Negocio negocio = new Negocio();
            negocio.setNombre(dto.getNombreNegocio().trim());
            negocio = negocioRepo.save(negocio);

            // Crear administrador asociado al negocio.
            Usuario usuario = new Usuario();
            usuario.setNombre(dto.getNombre().trim());
            usuario.setEmail(email);
            usuario.setPassword(
                    passwordEncoder.encode(dto.getPassword())
            );
            usuario.setRol("ADMIN");
            usuario.setNegocioId(negocio.getId());

            usuarioRepo.save(usuario);

            return "REGISTRO_OK";

        } catch (Exception e) {
            System.err.println("Error durante el registro de MARAV.");
            System.err.println("Tipo: " + e.getClass().getName());
            System.err.println("Detalle: " + e.getMessage());
            e.printStackTrace(System.err);

            return "ERROR_SERVIDOR";
        }
    }

    // =========================================================
    // VISTA DE RECUPERACIÓN
    // =========================================================

    @GetMapping("/recuperar")
    public String recuperarPassword() {
        return "recuperar";
    }

    // =========================================================
    // SOLICITAR RECUPERACIÓN DE CONTRASEÑA
    // =========================================================

    @PostMapping("/enviar-recuperacion")
    public String enviarRecuperacion(
            @RequestParam String email
    ) {

        if (email == null || email.trim().isEmpty()) {
            return "redirect:/recuperar?error";
        }

        String correo = email.trim().toLowerCase();

        Usuario usuario = usuarioRepo
                .findByEmail(correo)
                .orElse(null);

        if (usuario == null) {
            return "redirect:/recuperar?error";
        }

        // Generar un token único con vigencia de 15 minutos.
        String token = UUID.randomUUID().toString();

        usuario.setToken(token);
        usuario.setTokenExpiracion(
                LocalDateTime.now().plusMinutes(15)
        );

        usuarioRepo.save(usuario);

        try {
            // Validar la URL configurada para construir el enlace.
            if (appBaseUrl == null || appBaseUrl.isBlank()) {
                throw new IllegalStateException(
                        "APP_BASE_URL no está configurada."
                );
            }

            String baseUrl = appBaseUrl.trim()
                    .replaceAll("/+$", "");

            String enlace = baseUrl
                    + "/reset-password?token="
                    + URLEncoder.encode(
                    token,
                    StandardCharsets.UTF_8
            );

            // Enviar correo mediante Resend.
            emailService.enviarRecuperacion(
                    usuario.getEmail(),
                    enlace
            );

            return "redirect:/recuperar?enviado";

        } catch (Exception e) {

            // Registrar la causa real del error en Railway.
            // No imprimir API keys ni contraseñas.
            System.err.println(
                    "ERROR COMPLETO AL ENVIAR RECUPERACIÓN EN MARAV"
            );
            System.err.println(
                    "Tipo: " + e.getClass().getName()
            );
            System.err.println(
                    "Detalle: " + e.getMessage()
            );
            e.printStackTrace(System.err);

            // Si el envío falla, invalidar el token generado.
            try {
                usuario.setToken(null);
                usuario.setTokenExpiracion(null);
                usuarioRepo.save(usuario);

            } catch (Exception errorGuardar) {
                System.err.println(
                        "No se pudo invalidar el token de recuperación."
                );
                System.err.println(
                        "Tipo: " + errorGuardar.getClass().getName()
                );
                System.err.println(
                        "Detalle: " + errorGuardar.getMessage()
                );
                errorGuardar.printStackTrace(System.err);
            }

            return "redirect:/recuperar?errorEnvio";
        }
    }

    // =========================================================
    // VALIDAR TOKEN Y MOSTRAR FORMULARIO
    // =========================================================

    @GetMapping("/reset-password")
    public String resetPassword(
            @RequestParam String token
    ) {

        Usuario usuario = usuarioRepo
                .findByToken(token)
                .orElse(null);

        if (usuario == null) {
            return "redirect:/login?tokenInvalido";
        }

        if (usuario.getTokenExpiracion() == null
                || usuario.getTokenExpiracion()
                .isBefore(LocalDateTime.now())) {
            return "redirect:/login?tokenExpirado";
        }

        return "reset-password";
    }

    // =========================================================
    // GUARDAR NUEVA CONTRASEÑA
    // =========================================================

    @PostMapping("/guardar-password")
    public String guardarPassword(
            @RequestParam String token,
            @RequestParam String password
    ) {

        Usuario usuario = usuarioRepo
                .findByToken(token)
                .orElse(null);

        if (usuario == null) {
            return "redirect:/login?tokenInvalido";
        }

        if (usuario.getTokenExpiracion() == null
                || usuario.getTokenExpiracion()
                .isBefore(LocalDateTime.now())) {
            return "redirect:/login?tokenExpirado";
        }

        if (password == null || password.isBlank()) {
            return "redirect:/login?passwordInvalido";
        }

        // Guardar la contraseña cifrada.
        usuario.setPassword(
                passwordEncoder.encode(password)
        );

        // El token solo se puede utilizar una vez.
        usuario.setToken(null);
        usuario.setTokenExpiracion(null);

        usuarioRepo.save(usuario);

        return "redirect:/login?passwordActualizado";
    }
}
