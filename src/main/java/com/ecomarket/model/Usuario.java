// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.model;

// ======================================================
// 📚 IMPORTS
// ======================================================
import jakarta.persistence.*;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * ======================================================
 * 🔐 ENTIDAD USUARIO
 * ======================================================
 *
 * Maneja:
 *
 * ✔ Login
 * ✔ Roles
 * ✔ Estado usuario
 * ✔ Verificación
 * ✔ Recuperación contraseña
 * ✔ Relación con negocio
 *
 * ======================================================
 */
@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    // ======================================================
    // 🔑 ID
    // ======================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ======================================================
    // 👤 NOMBRE COMPLETO
    // ======================================================
    @Column(nullable = false)
    private String nombre;

    // ======================================================
    // 🪪 TIPO IDENTIFICACIÓN
    // ======================================================
    @Column(name = "tipo_identificacion")
    private String tipoIdentificacion;

    // ======================================================
    // 🪪 NÚMERO IDENTIFICACIÓN
    // ======================================================
    @Column(
            name = "numero_identificacion",
            unique = true
    )
    private String numeroIdentificacion;

    // ======================================================
    // 📧 EMAIL LOGIN
    // ======================================================
    @Column(
            nullable = false,
            unique = true
    )
    private String email;

    // ======================================================
    // 🔐 PASSWORD ENCRIPTADA
    // ======================================================
    @Column(nullable = false)
    private String password;

    // ======================================================
    // 👮 ROL
    // ======================================================
    //
    // ADMIN
    // EMPLEADO
    //
    // ======================================================
    @Column(nullable = false)
    private String rol;

    // ======================================================
    // ✅ CORREO VERIFICADO
    // ======================================================
    private Boolean verificado = false;

    // ======================================================
    // 🔁 TOKEN RECUPERACIÓN PASSWORD
    // ======================================================
    //
    // Se genera cuando el usuario solicita
    // recuperar la contraseña.
    //
    // Ejemplo:
    //
    // 9c9f2c34-9e5f-4b54-a7f1-xxxxx
    //
    // ======================================================
    private String token;

    // ======================================================
    // ⏳ FECHA EXPIRACIÓN TOKEN
    // ======================================================
    //
    // El token tendrá una duración
    // de 15 o 30 minutos.
    //
    // ======================================================
    @Column(name = "token_expiracion")
    private LocalDateTime tokenExpiracion;

    // ======================================================
    // 🔥 ESTADO USUARIO
    // ======================================================
    //
    // true  = activo
    // false = bloqueado
    //
    // ======================================================
    private Boolean estado = true;

    // ======================================================
    // 📅 FECHA CREACIÓN
    // ======================================================
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // ======================================================
    // 🏪 NEGOCIO
    // ======================================================
    //
    // Relación lógica con la tabla negocios
    //
    // ======================================================
    @Column(name = "negocio_id")
    private Integer negocioId;

    // ======================================================
    // ⚡ ANTES DE INSERTAR
    // ======================================================
    @PrePersist
    public void prePersist() {

        if(fechaCreacion == null){
            fechaCreacion = LocalDateTime.now();
        }

        if(estado == null){
            estado = true;
        }

        if(verificado == null){
            verificado = false;
        }
    }
}
