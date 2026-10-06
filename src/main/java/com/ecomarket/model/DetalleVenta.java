// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.model;

// ======================================================
// 📚 IMPORTS
// ======================================================
import jakarta.persistence.*;

/**
 * ======================================================
 * 🧾 ENTIDAD DETALLE VENTA
 * ======================================================
 *
 * Representa los productos vendidos
 * dentro de una venta.
 *
 * ✔ Historial de ventas
 * ✔ Facturación
 * ✔ Tickets
 * ✔ Reportes
 * ✔ Kardex
 *
 * ======================================================
 */
@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {

    // ==================================================
    // 🔹 ID AUTOINCREMENTAL
    // ==================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ==================================================
    // 🧾 ID VENTA
    // ==================================================
    @Column(name = "id_venta")
    private Integer idVenta;

    // ==================================================
    // 📦 ID PRODUCTO
    // ==================================================
    @Column(name = "id_producto")
    private Integer idProducto;

    // ==================================================
    // 🏷 NOMBRE PRODUCTO
    // Se guarda histórico
    // ==================================================
    @Column(name = "nombre_producto")
    private String nombreProducto;

    // ==================================================
    // 🔢 CANTIDAD
    // ==================================================
    private Integer cantidad;

    // ==================================================
    // 💰 PRECIO UNITARIO
    // ==================================================
    private Double precio;

    // ==================================================
    // 💵 SUBTOTAL
    // ==================================================
    private Double subtotal;

    // ==================================================
    // 🔥 MÉTODO AUTOMÁTICO
    // ==================================================
    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {

        // 🔥 calcular subtotal automáticamente
        if (cantidad != null && precio != null) {

            subtotal = cantidad * precio;
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

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(
            String nombreProducto
    ) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}

