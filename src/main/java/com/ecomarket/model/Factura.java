package com.ecomarket.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * ======================================================
 * 🧾 ENTIDAD FACTURA
 * ======================================================
 *
 * ✔ Facturación
 * ✔ IVA
 * ✔ Totales
 * ✔ Historial
 * ✔ Reportes
 *
 * ======================================================
 */

@Entity
@Table(name = "facturas")
public class Factura {

    // ==================================================
    // 🔹 ID
    // ==================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ==================================================
    // 🧾 ID VENTA
    // ==================================================
    @Column(name = "venta_id",
            nullable = false)
    private Integer ventaId;

    // ==================================================
    // 🧾 NÚMERO FACTURA
    // ==================================================
    @Column(name = "numero_factura",
            unique = true)
    private String numeroFactura;

    // ==================================================
    // 💰 SUBTOTAL
    // ==================================================
    private Double subtotal;

    // ==================================================
    // 💰 IVA
    // ==================================================
    private Double iva;

    // ==================================================
    // ✅ APLICAR IVA
    // ==================================================
    @Column(name = "aplicar_iva")
    private Boolean aplicarIva;

    // ==================================================
    // 💵 TOTAL
    // ==================================================
    private Double total;

    // ==================================================
    // 📅 FECHA
    // ==================================================
    private LocalDateTime fecha;

    // ==================================================
    // 📦 ESTADO
    // ==================================================
    private String estado;

    // ==================================================
    // 📝 OBSERVACIÓN
    // ==================================================
    private String observacion;

    // ==================================================
    // 🔥 PREPERSIST
    // ==================================================
    @PrePersist
    public void prePersist() {

        if(fecha == null){
            fecha = LocalDateTime.now();
        }

        if(estado == null){
            estado = "GENERADA";
        }

        if(aplicarIva == null){
            aplicarIva = false;
        }

        if(subtotal == null){
            subtotal = 0.0;
        }

        if(iva == null){
            iva = 0.0;
        }

        if(total == null){
            total = 0.0;
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

    public Integer getVentaId() {
        return ventaId;
    }

    public void setVentaId(Integer ventaId) {
        this.ventaId = ventaId;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getIva() {
        return iva;
    }

    public void setIva(Double iva) {
        this.iva = iva;
    }

    public Boolean getAplicarIva() {
        return aplicarIva;
    }

    public void setAplicarIva(Boolean aplicarIva) {
        this.aplicarIva = aplicarIva;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}


