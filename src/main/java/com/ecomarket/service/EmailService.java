
        package com.ecomarket.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY:}")
    private String apiKey;

    @Value("${RESEND_FROM_EMAIL:onboarding@resend.dev}")
    private String remitente;

    /**
     * Envía el enlace de recuperación de contraseña mediante Resend.
     * No utiliza Gmail ni JavaMailSender.
     */
    public void enviarRecuperacion(String destinatario, String enlace) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar la variable RESEND_API_KEY en Railway."
            );
        }

        if (destinatario == null || destinatario.isBlank()) {
            throw new IllegalArgumentException(
                    "El correo destinatario está vacío."
            );
        }

        if (enlace == null || enlace.isBlank()) {
            throw new IllegalArgumentException(
                    "El enlace de recuperación está vacío."
            );
        }

        try {
            Resend resend = new Resend(apiKey);

            String html = """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0;padding:24px;
                             font-family:Arial,sans-serif;
                             background:#f3f4f6;color:#1f2937;">
                    <div style="max-width:560px;margin:auto;
                                padding:30px;background:#ffffff;
                                border-radius:12px;">
                        <h1 style="color:#047857;">MARAV</h1>

                        <h2>Recuperación de contraseña</h2>

                        <p>
                            Recibimos una solicitud para cambiar la
                            contraseña de tu cuenta.
                        </p>

                        <p>
                            Haz clic en el siguiente botón para crear
                            una nueva contraseña:
                        </p>

                        <p style="margin:28px 0;">
                            <a href="%s"
                               style="display:inline-block;
                                      background:#047857;
                                      color:#ffffff;
                                      padding:14px 22px;
                                      border-radius:8px;
                                      text-decoration:none;
                                      font-weight:bold;">
                                Restablecer contraseña
                            </a>
                        </p>

                        <p>
                            Este enlace vence en 15 minutos.
                            Si no solicitaste el cambio, puedes ignorar
                            este mensaje.
                        </p>

                        <p style="font-size:12px;color:#6b7280;">
                            Por seguridad, no compartas este enlace.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(escaparHtml(enlace));

            CreateEmailOptions correo = CreateEmailOptions.builder()
                    .from("MARAV <" + remitente + ">")
                    .to(destinatario.trim())
                    .subject("Recuperación de contraseña - MARAV")
                    .html(html)
                    .build();

            CreateEmailResponse respuesta =
                    resend.emails().send(correo);

            if (respuesta == null || respuesta.getId() == null) {
                throw new IllegalStateException(
                        "Resend no confirmó el envío del correo."
                );
            }

            System.out.println(
                    "Correo de recuperación aceptado por Resend. ID: "
                            + respuesta.getId()
            );

        } catch (ResendException e) {
            System.err.println("ERROR AL ENVIAR CORREO CON RESEND");
            System.err.println("Detalle: " + e.getMessage());
            e.printStackTrace(System.err);

            throw new IllegalStateException(
                    "Resend no pudo enviar el correo de recuperación.", e
            );
        }
    }

    private String escaparHtml(String texto) {
        return texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}


