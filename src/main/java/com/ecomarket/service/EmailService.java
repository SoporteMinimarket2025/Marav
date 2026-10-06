package com.ecomarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarRecuperacion(
            String destinatario,
            String enlace
    ) {

        SimpleMailMessage mensaje =
                new SimpleMailMessage();

        mensaje.setFrom("soporte.minimarket2025@gmail.com");

        mensaje.setTo(destinatario);

        mensaje.setSubject(
                "Recuperación de contraseña - EcoMarket PRO"
        );

        mensaje.setText(
                "Hola.\n\n" +
                        "Hemos recibido una solicitud para recuperar tu contraseña.\n\n" +
                        "Haz clic en el siguiente enlace:\n\n" +
                        enlace +
                        "\n\n" +
                        "Este enlace expirará en 15 minutos.\n\n" +
                        "Si no realizaste esta solicitud, ignora este correo."
        );

        mailSender.send(mensaje);
    }
}