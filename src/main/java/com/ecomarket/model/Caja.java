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
 * 💰 ENTIDAD CAJA
 * ======================================================
 *
 * La caja NO necesita tener un negocioId directamente.
 *
 * La relación del negocio se obtiene mediante:
 *
 * Caja.idUsuario
 *      ↓
 * Usuario.id
 *      ↓
 * Usuario.negocioId
 *
 * Esto permite mantener la estructura actual de la base
 * de datos y aplicar aislamiento por negocio desde el
 * Repository y Service.
 *
 * ======================================================
 */
@Entity
@Table(name = "cajas")
public class Caja {

    // ==================================================
    // 🔹 ID
    // ==================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    // ==================================================
    // 👤 USUARIO QUE ABRIÓ LA CAJA
    // ==================================================

    @Column(name = "id_usuario")
    private Integer idUsuario;


    // ==================================================
    // 📅 FECHA DE APERTURA
    // ==================================================

    @Column(name = "fecha_apertura")
    private LocalDateTime fechaApertura;


    // ==================================================
    // 📅 FECHA DE CIERRE
    // ==================================================

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;


    // ==================================================
    // 💰 SALDO INICIAL
    // ==================================================

    @Column(name = "saldo_inicial")
    private Double saldoInicial = 0.0;


    // ==================================================
    // 💵 INGRESOS
    // ==================================================

    @Column(nullable = false)
    private Double ingresos = 0.0;


    // ==================================================
    // 💸 EGRESOS
    // ==================================================

    @Column(nullable = false)
    private Double egresos = 0.0;


    // ==================================================
    // 💰 SALDO FINAL
    // ==================================================

    @Column(name = "saldo_final")
    private Double saldoFinal = 0.0;


    // ==================================================
    // 📦 ESTADO
    // ==================================================

    @Column(length = 20)
    private String estado = "ABIERTA";


    // ==================================================
    // 🔥 PREPERSIST
    // ==================================================
    //
    // Se ejecuta antes de guardar una nueva caja.
    //
    // Si no existe fecha de apertura, se asigna
    // automáticamente la fecha y hora actual.
    //
    // ==================================================

    @PrePersist
    public void prePersist() {

        if (fechaApertura == null) {
            fechaApertura = LocalDateTime.now();
        }

        if (saldoInicial == null) {
            saldoInicial = 0.0;
        }

        if (ingresos == null) {
            ingresos = 0.0;
        }

        if (egresos == null) {
            egresos = 0.0;
        }

        if (saldoFinal == null) {
            saldoFinal = saldoInicial;
        }

        if (estado == null || estado.isBlank()) {
            estado = "ABIERTA";
        }
    }


    // ==================================================
    // 🔹 GETTERS & SETTERS
    // ==================================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }


    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }


    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }


    public Double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(Double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }


    public Double getIngresos() {
        return ingresos;
    }

    public void setIngresos(Double ingresos) {
        this.ingresos = ingresos;
    }


    public Double getEgresos() {
        return egresos;
    }

    public void setEgresos(Double egresos) {
        this.egresos = egresos;
    }


    public Double getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(Double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }


    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}