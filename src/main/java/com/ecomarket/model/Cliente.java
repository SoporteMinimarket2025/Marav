
// ======================================================
// 📁 PACKAGE
// ======================================================
        package com.ecomarket.model;

// ======================================================
// 📚 IMPORTS
// ======================================================
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * ======================================================
 * 👥 ENTIDAD CLIENTE
 * ======================================================
 *
 * Representa la tabla clientes en MySQL.
 *
 * ✔ CRUD clientes
 * ✔ Ventas a crédito
 * ✔ Abonos
 * ✔ Historial
 * ✔ WhatsApp
 * ✔ Aislamiento por negocio
 *
 * ======================================================
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    // ==================================================
    // 🔹 ID AUTOINCREMENTAL
    // ==================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ==================================================
    // 👤 NOMBRE CLIENTE
    // ==================================================
    @Column(nullable = false, length = 100)
    private String nombre;

    // ==================================================
    // 🪪 TIPO IDENTIFICACIÓN
    // ==================================================
    @Column(name = "tipo_identificacion")
    private String tipoIdentificacion;

    // ==================================================
    // 🔢 NÚMERO IDENTIFICACIÓN
    // ==================================================
    @Column(name = "numero_identificacion")
    private String numeroIdentificacion;

    // ==================================================
    // 📞 TELÉFONO
    // ==================================================
    private String telefono;

    // ==================================================
    // 📍 DIRECCIÓN
    // ==================================================
    private String direccion;

    // ==================================================
    // 💳 DEUDA ACUMULADA
    // ==================================================
    private Double deuda;

    // ==================================================
    // ✅ ESTADO
    // true = activo
    // false = eliminado
    // ==================================================
    private Boolean estado;

    // ==================================================
    // 🏪 NEGOCIO AL QUE PERTENECE
    // ==================================================
    //
    // 🔐 ESTE CAMPO ES FUNDAMENTAL PARA EL MULTI-NEGOCIO.
    //
    // Cada cliente pertenece a UN SOLO negocio.
    //
    // Ejemplo:
    //
    // Cliente Juan → negocioId = 1
    // Cliente Pedro → negocioId = 2
    //
    // Así el negocio 1 nunca debe consultar
    // los clientes del negocio 2.
    //
    // ==================================================
    @Column(name = "negocio_id")
    private Integer negocioId;

    // ==================================================
    // 📅 FECHA CREACIÓN
    // ==================================================
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // ==================================================
    // 🔥 MÉTODO AUTOMÁTICO
    // ==================================================
    @PrePersist
    public void prePersist() {

        // 📅 Fecha actual
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }

        // 💳 Deuda inicial
        if (deuda == null) {
            deuda = 0.0;
        }

        // ✅ Activo por defecto
        if (estado == null) {
            estado = true;
        }
    }

    // ==================================================
    // GETTERS & SETTERS
    // ==================================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Double getDeuda() {
        return deuda;
    }

    public void setDeuda(Double deuda) {
        this.deuda = deuda;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    // ==================================================
    // 🏪 GET / SET NEGOCIO
    // ==================================================

    public Integer getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Integer negocioId) {
        this.negocioId = negocioId;
    }

    // ==================================================
    // 📅 FECHA
    // ==================================================

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(
            LocalDateTime fechaCreacion
    ) {
        this.fechaCreacion = fechaCreacion;
    }
}

