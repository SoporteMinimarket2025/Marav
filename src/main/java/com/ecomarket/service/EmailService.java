
// ======================================================
// 📧 SERVICIO DE CORREO - MARAV
// ======================================================
// Proveedor: Resend
// Función: Enviar enlaces de recuperación de contraseña
// ======================================================

        package com.ecomarket.service;

// ======================================================
// 📚 IMPORTACIONES
// ======================================================

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// ======================================================
// ⚙️ SERVICIO
// ======================================================

@Service
public class EmailService {

    // ==================================================
    // 🔑 CONFIGURACIÓN DESDE LAS VARIABLES DE RAILWAY
    // ==================================================

    @Value("${RESEND_API_KEY:}")
    private String apiKey;

    @Value("${RESEND_FROM_EMAIL:onboarding@resend.dev}")
    private String remitente;

    // ==================================================
    // 📩 ENVIAR CORREO DE RECUPERACIÓN
    // ==================================================

    public void enviarRecuperacion(String destinatario, String enlace) {

        // ----------------------------------------------
        // 1. VALIDAR LA CLAVE DE RESEND
        // ----------------------------------------------

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar RESEND_API_KEY en las variables de Railway."
            );
        }

        // Eliminar únicamente espacios al principio y al final.
        // No modificar silenciosamente el contenido interno de la clave.
        String apiKeyLimpia = apiKey.trim();

        // Detectar saltos de línea dentro de la clave.
        if (apiKeyLimpia.contains("\n")
                || apiKeyLimpia.contains("\r")) {

            throw new IllegalStateException(
                    "RESEND_API_KEY contiene saltos de línea. "
                            + "Corrige la variable en Railway y vuelve a desplegar."
            );
        }

        // Evitar configurar por error la clave con el prefijo Bearer.
        if (apiKeyLimpia.regionMatches(
                true, 0, "Bearer ", 0, 7)) {

            throw new IllegalStateException(
                    "RESEND_API_KEY debe contener solamente la clave "
                            + "de Resend, sin el prefijo Bearer."
            );
        }

        // ----------------------------------------------
        // 2. VALIDAR EL CORREO DESTINATARIO
        // ----------------------------------------------

        if (destinatario == null || destinatario.isBlank()) {
            throw new IllegalArgumentException(
                    "El correo destinatario está vacío."
            );
        }

        String destinatarioLimpio = destinatario.trim();

        if (destinatarioLimpio.contains("\n")
                || destinatarioLimpio.contains("\r")) {

            throw new IllegalArgumentException(
                    "El correo destinatario contiene caracteres inválidos."
            );
        }

        // ----------------------------------------------
        // 3. VALIDAR EL ENLACE DE RECUPERACIÓN
        // ----------------------------------------------

        if (enlace == null || enlace.isBlank()) {
            throw new IllegalArgumentException(
                    "El enlace de recuperación está vacío."
            );
        }

        String enlaceLimpio = enlace.trim();

        if (enlaceLimpio.contains("\n")
                || enlaceLimpio.contains("\r")) {

            throw new IllegalArgumentException(
                    "El enlace de recuperación contiene caracteres inválidos."
            );
        }

        // ----------------------------------------------
        // 4. VALIDAR EL REMITENTE
        // ----------------------------------------------

        if (remitente == null || remitente.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar RESEND_FROM_EMAIL en Railway."
            );
        }

        String remitenteLimpio = remitente.trim();

        if (remitenteLimpio.contains("\n")
                || remitenteLimpio.contains("\r")
                || remitenteLimpio.contains("<")
                || remitenteLimpio.contains(">")) {

            throw new IllegalStateException(
                    "RESEND_FROM_EMAIL tiene un formato inválido."
            );
        }

        // ----------------------------------------------
        // 5. CREAR EL CLIENTE DE RESEND Y ENVIAR
        // ----------------------------------------------

        try {

            // Crear conexión con Resend.
            Resend resend = new Resend(apiKeyLimpia);

            // Preparar el contenido HTML del correo.
            String html = """
                    <!DOCTYPE html>
                    <html lang="es">

                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport"
                              content="width=device-width, initial-scale=1.0">
                        <title>Recuperación de contraseña - MARAV</title>
                    </head>

                    <body style="
                        margin: 0;
                        padding: 24px;
                        font-family: Arial, sans-serif;
                        background-color: #f3f4f6;
                        color: #1f2937;
                    ">

                        <div style="
                            max-width: 560px;
                            margin: 0 auto;
                            padding: 30px;
                            background-color: #ffffff;
                            border-radius: 12px;
                            box-shadow: 0 4px 15px rgba(0,0,0,0.08);
                        ">

                            <h1 style="
                                margin-top: 0;
                                color: #047857;
                                text-align: center;
                            ">
                                MARAV
                            </h1>

                            <h2 style="color: #111827;">
                                Recuperación de contraseña
                            </h2>

                            <p>
                                Recibimos una solicitud para cambiar
                                la contraseña de tu cuenta de MARAV.
                            </p>

                            <p>
                                Para establecer una nueva contraseña,
                                haz clic en el siguiente botón:
                            </p>

                            <div style="
                                margin: 30px 0;
                                text-align: center;
                            ">

                                <a href="%s" style="
                                    display: inline-block;
                                    padding: 14px 24px;
                                    background-color: #047857;
                                    color: #ffffff;
                                    border-radius: 8px;
                                    text-decoration: none;
                                    font-weight: bold;
                                ">
                                    Restablecer contraseña
                                </a>

                            </div>

                            <p>
                                Este enlace vence en 15 minutos.
                            </p>

                            <p>
                                Si no solicitaste recuperar tu contraseña,
                                puedes ignorar este mensaje.
                            </p>

                            <hr style="
                                margin-top: 28px;
                                border: none;
                                border-top: 1px solid #e5e7eb;
                            ">

                            <p style="
                                font-size: 12px;
                                color: #6b7280;
                                text-align: center;
                            ">
                                MARAV - Gestión para pequeños negocios.
                                <br>
                                Por seguridad, no compartas este enlace.
                            </p>

                        </div>

                    </body>
                    </html>
                    """.formatted(escaparHtml(enlaceLimpio));

            // Configurar el mensaje.
            CreateEmailOptions correo = CreateEmailOptions.builder()
                    .from("MARAV <" + remitenteLimpio + ">")
                    .to(destinatarioLimpio)
                    .subject("Recuperación de contraseña - MARAV")
                    .html(html)
                    .build();

            // Enviar el correo a través de Resend.
            CreateEmailResponse respuesta =
                    resend.emails().send(correo);

            // ------------------------------------------
            // 6. COMPROBAR LA RESPUESTA
            // ------------------------------------------

            if (respuesta == null || respuesta.getId() == null) {

                throw new IllegalStateException(
                        "Resend no confirmó el envío del correo."
                );
            }

            // Registrar únicamente el identificador del envío.
            // Nunca registrar la clave API.
            System.out.println(
                    "MARAV: correo aceptado por Resend. ID: "
                            + respuesta.getId()
            );

        } catch (ResendException e) {

            // ------------------------------------------
            // 7. REGISTRAR EL ERROR DE RESEND
            // ------------------------------------------

            System.err.println(
                    "MARAV: error al enviar correo mediante Resend."
            );

            System.err.println(
                    "Tipo de error: " + e.getClass().getSimpleName()
            );

            System.err.println(
                    "Detalle: " + e.getMessage()
            );

            // Conservar el error original para facilitar el diagnóstico.
            throw new IllegalStateException(
                    "Resend no pudo enviar el correo de recuperación.",
                    e
            );
        }
    }

    // ==================================================
    // 🛡️ ESCAPAR TEXTO PARA INSERTARLO EN HTML
    // ==================================================

    private String escaparHtml(String texto) {

        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
