package com.ecomarket.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * =====================================================
 * 📦 ENTIDAD PRODUCTO
 * =====================================================
 * Representa la tabla productos en MySQL
 *
 * Relación:
 * Producto -> Proveedor
 *
 * Tabla:
 * productos
 * =====================================================
 */
@Entity
@Table(name = "productos")
public class Producto {

    // =====================================================
    // 🔹 ID AUTOINCREMENTAL
    // =====================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // =====================================================
    // 🔹 NOMBRE PRODUCTO
    // =====================================================
    @Column(nullable = false, length = 100)
    private String nombre;

    // =====================================================
    // 🔹 DESCRIPCIÓN
    // =====================================================
    @Column(length = 150)
    private String descripcion;

    // =====================================================
    // 🔹 PRECIO
    // =====================================================
    @Column(nullable = false)
    private Double precio;

    // =====================================================
    // 🔹 ESTADO
    // true = activo
    // false = eliminado lógico
    // =====================================================
    private Boolean estado;

    // =====================================================
    // 🔹 FECHA CREACIÓN
    // =====================================================
    @Column(
            name = "fecha_creacion",
            updatable = false
    )
    private LocalDateTime fechaCreacion;

    // =====================================================
    // 🔹 NEGOCIO
    // =====================================================
    @Column(
            name = "negocio_id"
    )
    private Integer negocioId;

    // =====================================================
    // 🔹 PROVEEDOR
    // FK -> proveedor_id
    // =====================================================
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    // =====================================================
    // 🔥 CONSTRUCTOR VACÍO
    // =====================================================
    public Producto() {
    }

    // =====================================================
    // 🔥 CONSTRUCTOR COMPLETO
    // =====================================================
    public Producto(
            Integer id,
            String nombre,
            String descripcion,
            Double precio,
            Boolean estado,
            LocalDateTime fechaCreacion,
            Integer negocioId,
            Proveedor proveedor
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.negocioId = negocioId;
        this.proveedor = proveedor;
    }

    // =====================================================
    // 🔥 PRE PERSIST
    // =====================================================
    @PrePersist
    public void prePersist() {

        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }

        if (this.estado == null) {
            this.estado = true;
        }
    }

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(
            LocalDateTime fechaCreacion
    ) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(
            Integer negocioId
    ) {
        this.negocioId = negocioId;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(
            Proveedor proveedor
    ) {
        this.proveedor = proveedor;
    }
}

