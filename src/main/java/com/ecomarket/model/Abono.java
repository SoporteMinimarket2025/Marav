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
 * 💰 ENTIDAD ABONO
 * ======================================================
 *
 * Representa la tabla "abonos" de MySQL.
 *
 * Campos:
 *
 * id
 * id_cliente
 * monto
 * fecha
 * descripcion
 * id_venta
 *
 * ======================================================
 *
 * FUNCIONES:
 *
 * ✔ Registrar abonos
 * ✔ Consultar historial
 * ✔ Relacionar abono con cliente
 * ✔ Relacionar abono con venta
 * ✔ Controlar deuda del cliente
 *
 * ======================================================
 */
@Entity
@Table(name = "abonos")
public class Abono {

    // ==================================================
    // 🔹 ID AUTOINCREMENTAL
    // ==================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ==================================================
    // 👤 CLIENTE
    //
    // Guardamos únicamente el ID porque así está
    // construida actualmente tu base de datos.
    // ==================================================
    @Column(name = "id_cliente")
    private Integer idCliente;

    // ==================================================
    // 💰 MONTO DEL ABONO
    // ==================================================
    @Column(nullable = false)
    private Double monto;

    // ==================================================
    // 📅 FECHA
    // ==================================================
    @Column(
            name = "fecha",
            insertable = false,
            updatable = false
    )
    private LocalDateTime fecha;

    // ==================================================
    // 📝 DESCRIPCIÓN
    // ==================================================
    @Column(length = 150)
    private String descripcion;

    // ==================================================
    // 🧾 VENTA RELACIONADA
    //
    // Puede ser NULL.
    // ==================================================
    @Column(name = "id_venta")
    private Integer idVenta;


    // ==================================================
    // 🔥 PRE-PERSIST
    // ==================================================
    @PrePersist
    public void prePersist() {

        // ----------------------------------------------
        // 💰 Monto por defecto
        // ----------------------------------------------
        if (monto == null) {

            monto = 0.0;
        }

        // ----------------------------------------------
        // 📅 Si MySQL no genera la fecha por alguna
        // razón, usamos la fecha actual.
        // ----------------------------------------------
        if (fecha == null) {

            fecha = LocalDateTime.now();
        }
    }


    // ==================================================
    // GETTERS Y SETTERS
    // ==================================================

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {

        this.id = id;
    }


    public Integer getIdCliente() {

        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {

        this.idCliente = idCliente;
    }


    public Double getMonto() {

        return monto;
    }

    public void setMonto(Double monto) {

        this.monto = monto;
    }


    public LocalDateTime getFecha() {

        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {

        this.fecha = fecha;
    }


    public String getDescripcion() {

        return descripcion;
    }

    public void setDescripcion(String descripcion) {

        this.descripcion = descripcion;
    }


    public Integer getIdVenta() {

        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {

        this.idVenta = idVenta;
    }
}

