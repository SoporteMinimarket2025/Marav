package com.ecomarket.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
public class Inventario {

    // =====================================================
    // ID
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    // =====================================================
    // PRODUCTO
    // =====================================================

    @Column(
            name = "producto_id",
            nullable = false
    )
    private Integer productoId;


    // =====================================================
    // STOCK ACTUAL
    // =====================================================

    @Column(name = "stock_actual")
    private Integer stockActual;


    // =====================================================
    // STOCK MINIMO
    // =====================================================

    @Column(name = "stock_minimo")
    private Integer stockMinimo;


    // =====================================================
    // UBICACION
    // =====================================================

    @Column(name = "ubicacion")
    private String ubicacion;


    // =====================================================
    // LOTE
    // =====================================================

    @Column(name = "lote")
    private String lote;


    // =====================================================
    // FECHA VENCIMIENTO
    // =====================================================

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;


    // =====================================================
    // FECHA ACTUALIZACION
    // =====================================================

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;


    // =====================================================
    // PRE PERSIST
    // =====================================================

    @PrePersist
    public void prePersist() {

        fechaActualizacion =
                LocalDateTime.now();


        if (stockActual == null) {

            stockActual = 0;

        }


        if (stockMinimo == null) {

            stockMinimo = 5;

        }

    }


    // =====================================================
    // PRE UPDATE
    // =====================================================

    @PreUpdate
    public void preUpdate() {

        fechaActualizacion =
                LocalDateTime.now();

    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getId() {

        return id;

    }


    public void setId(Integer id) {

        this.id = id;

    }


    public Integer getProductoId() {

        return productoId;

    }


    public void setProductoId(
            Integer productoId
    ) {

        this.productoId =
                productoId;

    }


    public Integer getStockActual() {

        return stockActual;

    }


    public void setStockActual(
            Integer stockActual
    ) {

        this.stockActual =
                stockActual;

    }


    public Integer getStockMinimo() {

        return stockMinimo;

    }


    public void setStockMinimo(
            Integer stockMinimo
    ) {

        this.stockMinimo =
                stockMinimo;

    }


    public String getUbicacion() {

        return ubicacion;

    }


    public void setUbicacion(
            String ubicacion
    ) {

        this.ubicacion =
                ubicacion;

    }


    public String getLote() {

        return lote;

    }


    public void setLote(
            String lote
    ) {

        this.lote =
                lote;

    }


    public LocalDate getFechaVencimiento() {

        return fechaVencimiento;

    }


    public void setFechaVencimiento(
            LocalDate fechaVencimiento
    ) {

        this.fechaVencimiento =
                fechaVencimiento;

    }


    public LocalDateTime getFechaActualizacion() {

        return fechaActualizacion;

    }


    public void setFechaActualizacion(
            LocalDateTime fechaActualizacion
    ) {

        this.fechaActualizacion =
                fechaActualizacion;

    }

}

