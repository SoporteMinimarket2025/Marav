
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
    // CIFRADO DE CONTRASEÑAS
    // =========================================================

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================================================
    // SERVICIO DE CORREO RESEND
    // =========================================================

    @Autowired
    private EmailService emailService;

    // =========================================================
    // URL BASE DE MARAV
    // En local usa localhost.
    // En Railway se configura APP_BASE_URL.
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

            // Validar objeto recibido.
            if (dto == null) {
                return "DATOS_INVALIDOS";
            }

            // Validar nombre del negocio.
            if (dto.getNombreNegocio() == null
                    || dto.getNombreNegocio().trim().isEmpty()) {
                return "NEGOCIO_REQUERIDO";
            }

            // Validar nombre del administrador.
            if (dto.getNombre() == null
                    || dto.getNombre().trim().isEmpty()) {
                return "NOMBRE_REQUERIDO";
            }

            // Validar correo.
            if (dto.getEmail() == null
                    || dto.getEmail().trim().isEmpty()) {
                return "EMAIL_REQUERIDO";
            }

            // Validar contraseña.
            if (dto.getPassword() == null
                    || dto.getPassword().trim().isEmpty()) {
                return "PASSWORD_REQUERIDO";
            }

            // Normalizar correo.
            String email = dto.getEmail()
                    .trim()
                    .toLowerCase();

            // Comprobar si el correo ya está registrado.
            if (usuarioRepo.findByEmail(email).isPresent()) {
                return "EMAIL_EXISTE";
            }

            // Crear negocio.
            Negocio negocio = new Negocio();

            negocio.setNombre(
                    dto.getNombreNegocio().trim()
            );

            // Guardar negocio.
            negocio = negocioRepo.save(negocio);

            // Crear administrador.
            Usuario usuario = new Usuario();

            usuario.setNombre(
                    dto.getNombre().trim()
            );

            usuario.setEmail(email);

            // Guardar contraseña cifrada.
            usuario.setPassword(
                    passwordEncoder.encode(dto.getPassword())
            );

            // Asignar rol de administrador.
            usuario.setRol("ADMIN");

            // Asociar usuario con su negocio.
            usuario.setNegocioId(negocio.getId());

            // Guardar administrador.
            usuarioRepo.save(usuario);

            return "REGISTRO_OK";

        } catch (Exception e) {

            // No exponer detalles internos al cliente.
            System.err.println(
                    "Error durante el registro de MARAV: "
                            + e.getClass().getSimpleName()
            );

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

        // Normalizar correo para buscarlo.
        if (email == null || email.trim().isEmpty()) {
            return "redirect:/recuperar?error";
        }

        String correo = email.trim().toLowerCase();

        Usuario usuario = usuarioRepo
                .findByEmail(correo)
                .orElse(null);

        // Mantener el comportamiento actual.
        if (usuario == null) {
            return "redirect:/recuperar?error";
        }

        // =====================================================
        // GENERAR TOKEN
        // =====================================================

        String token = UUID.randomUUID().toString();

        usuario.setToken(token);

        usuario.setTokenExpiracion(
                LocalDateTime.now().plusMinutes(15)
        );

        usuarioRepo.save(usuario);

        // =====================================================
        // CONSTRUIR URL PÚBLICA
        // =====================================================

        String baseUrl = appBaseUrl.replaceAll("/+$", "");

        String enlace = baseUrl
                + "/reset-password?token="
                + URLEncoder.encode(
                token,
                StandardCharsets.UTF_8
        );

        // =====================================================
        // ENVIAR CORREO MEDIANTE RESEND
        // =====================================================

        try {

            emailService.enviarRecuperacion(
                    usuario.getEmail(),
                    enlace
            );

            return "redirect:/recuperar?enviado";

        } catch (Exception e) {

            // Invalidar el token si el envío falla.
            usuario.setToken(null);
            usuario.setTokenExpiracion(null);

            usuarioRepo.save(usuario);

            // No imprimir claves ni datos privados.
            System.err.println(
                    "Error de envío de recuperación: "
                            + e.getClass().getSimpleName()
            );

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

        // Validar token.
        if (usuario == null) {
            return "redirect:/login?tokenInvalido";
        }

        // Validar vencimiento.
        if (usuario.getTokenExpiracion() == null
                || usuario.getTokenExpiracion()
                .isBefore(LocalDateTime.now())) {

            return "redirect:/login?tokenExpirado";
        }

        // Validar que la nueva contraseña no esté vacía.
        if (password == null || password.isBlank()) {
            return "redirect:/login?passwordInvalido";
        }

        // Cifrar nueva contraseña.
        usuario.setPassword(
                passwordEncoder.encode(password)
        );

        // Invalidar token después de usarlo.
        usuario.setToken(null);
        usuario.setTokenExpiracion(null);

        // Guardar cambios.
        usuarioRepo.save(usuario);

        return "redirect:/login?passwordActualizado";
    }
}
