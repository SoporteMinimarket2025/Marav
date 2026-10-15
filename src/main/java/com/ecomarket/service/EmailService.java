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

    public void enviarRecuperacion(String destinatario, String enlace) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar RESEND_API_KEY."
            );
        }

        try {
            Resend resend = new Resend(apiKey);

            CreateEmailOptions correo = CreateEmailOptions.builder()
                    .from("MARAV <" + remitente + ">")
                    .to(destinatario)
                    .subject("Recuperación de contraseña - MARAV")
                    .html(
                            "<div style='font-family:Arial,sans-serif;"
                                    + "max-width:600px;margin:auto;color:#1f2937;'>"
                                    + "<h2 style='color:#047857;'>Recuperar contraseña</h2>"
                                    + "<p>Hola.</p>"
                                    + "<p>Recibimos una solicitud para recuperar "
                                    + "la contraseña de tu cuenta MARAV.</p>"
                                    + "<p>Utiliza el siguiente enlace para crear "
                                    + "una nueva contraseña:</p>"
                                    + "<p><a href='" + escaparHtml(enlace) + "' "
                                    + "style='background:#047857;color:white;"
                                    + "padding:12px 20px;text-decoration:none;"
                                    + "border-radius:6px;'>"
                                    + "Recuperar contraseña</a></p>"
                                    + "<p>Este enlace vence en 15 minutos.</p>"
                                    + "<p>Si no solicitaste este cambio, ignora "
                                    + "este mensaje.</p>"
                                    + "<p>Equipo MARAV</p>"
                                    + "</div>"
                    )
                    .build();

            CreateEmailResponse respuesta = resend.emails().send(correo);

            if (respuesta == null || respuesta.getId() == null) {
                throw new IllegalStateException(
                        "Resend no confirmó el envío del correo."
                );
            }

        } catch (ResendException e) {
            throw new IllegalStateException(
                    "Resend no pudo enviar el correo de recuperación.",
                    e
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