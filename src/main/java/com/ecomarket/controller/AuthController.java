
package com.ecomarket.controller;

// =========================================================
// 📚 IMPORTACIONES
// =========================================================

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
import org.springframework.ui.Model;
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

            // Crear negocio.
            Negocio negocio = new Negocio();
            negocio.setNombre(dto.getNombreNegocio().trim());

            negocio = negocioRepo.save(negocio);

            // Crear administrador.
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

            System.err.println("ERROR DURANTE EL REGISTRO DE MARAV");
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
            @RequestParam("email") String email
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

        // Generar token único.
        String token = UUID.randomUUID().toString();

        // Guardar token durante 15 minutos.
        usuario.setToken(token);

        usuario.setTokenExpiracion(
                LocalDateTime.now().plusMinutes(15)
        );

        usuarioRepo.save(usuario);

        try {

            // Validar URL pública.
            if (appBaseUrl == null || appBaseUrl.isBlank()) {
                throw new IllegalStateException(
                        "APP_BASE_URL no está configurada."
                );
            }

            String baseUrl = appBaseUrl.trim()
                    .replaceAll("/+$", "");

            // Construir enlace de recuperación.
            String enlace = baseUrl
                    + "/reset-password?token="
                    + URLEncoder.encode(
                    token,
                    StandardCharsets.UTF_8
            );

            System.out.println(
                    "MARAV: generando enlace de recuperación."
            );

            // Enviar correo mediante Resend.
            emailService.enviarRecuperacion(
                    usuario.getEmail(),
                    enlace
            );

            return "redirect:/recuperar?enviado";

        } catch (Exception e) {

            System.err.println(
                    "ERROR AL ENVIAR RECUPERACIÓN EN MARAV"
            );

            System.err.println(
                    "Tipo: " + e.getClass().getName()
            );

            System.err.println(
                    "Detalle: " + e.getMessage()
            );

            e.printStackTrace(System.err);

            // Invalidar token si falla el envío.
            try {

                usuario.setToken(null);
                usuario.setTokenExpiracion(null);

                usuarioRepo.save(usuario);

            } catch (Exception errorGuardar) {

                System.err.println(
                        "No se pudo invalidar el token."
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
            @RequestParam(value = "token", required = false)
            String token,
            Model model
    ) {

        if (token == null || token.isBlank()) {
            return "redirect:/login?tokenInvalido";
        }

        String tokenLimpio = token.trim();

        Usuario usuario = usuarioRepo
                .findByToken(tokenLimpio)
                .orElse(null);

        if (usuario == null) {

            System.err.println(
                    "MARAV: no se encontró el token de recuperación."
            );

            return "redirect:/login?tokenInvalido";
        }

        if (usuario.getTokenExpiracion() == null
                || usuario.getTokenExpiracion()
                .isBefore(LocalDateTime.now())) {

            return "redirect:/login?tokenExpirado";
        }

        // Enviar el token a la plantilla HTML.
        model.addAttribute("token", tokenLimpio);

        return "reset-password";
    }

    // =========================================================
    // GUARDAR NUEVA CONTRASEÑA
    // =========================================================

    @PostMapping("/guardar-password")
    public String guardarPassword(
            @RequestParam(value = "token", required = false)
            String token,
            @RequestParam(value = "password", required = false)
            String password
    ) {

        if (token == null || token.isBlank()) {
            return "redirect:/login?tokenInvalido";
        }

        String tokenLimpio = token.trim();

        Usuario usuario = usuarioRepo
                .findByToken(tokenLimpio)
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

        // Cifrar nueva contraseña.
        usuario.setPassword(
                passwordEncoder.encode(password)
        );

        // Invalidar token para impedir reutilización.
        usuario.setToken(null);
        usuario.setTokenExpiracion(null);

        usuarioRepo.save(usuario);

        System.out.println(
                "MARAV: contraseña actualizada correctamente."
        );

        return "redirect:/login?passwordActualizado";
    }
}
