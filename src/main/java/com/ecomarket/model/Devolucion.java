package com.ecomarket.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * ======================================================
 * ↩️ ENTIDAD DEVOLUCION
 * ======================================================
 *
 * Control de devoluciones de productos
 *
 * Relaciones:
 *
 * Devolucion -> Venta
 * Devolucion -> DetalleVenta
 * Devolucion -> Producto
 * Devolucion -> Cliente
 * Devolucion -> Usuario
 *
 * ======================================================
 */
@Entity
@Table(name = "devoluciones")
public class Devolucion {

    // ==================================================
    // ID
    // ==================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    // ==================================================
    // VENTA
    // ==================================================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "venta_id")
    private Venta venta;


    // ==================================================
    // DETALLE VENTA
    // ==================================================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "detalle_venta_id")
    private DetalleVenta detalleVenta;


    // ==================================================
    // PRODUCTO
    // ==================================================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id")
    private Producto producto;


    // ==================================================
    // CLIENTE
    // ==================================================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;


    // ==================================================
    // USUARIO
    // ==================================================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;


    // ==================================================
    // CANTIDAD
    // ==================================================

    @Column(nullable = false)
    private Integer cantidad;


    // ==================================================
    // MOTIVO
    // ==================================================

    @Column(length = 500)
    private String motivo;


    // ==================================================
    // MONTO
    // ==================================================

    @Column(nullable = false)
    private Double monto;


    // ==================================================
    // FECHA DEVOLUCION
    // ==================================================

    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;


    // ==================================================
    // ESTADO
    // ACTIVA / ANULADA
    // ==================================================

    @Column(length = 20)
    private String estado;


    // ==================================================
    // PRE PERSIST
    // ==================================================

    @PrePersist
    public void prePersist() {

        if (fechaDevolucion == null) {
            fechaDevolucion = LocalDateTime.now();
        }

        if (estado == null || estado.isBlank()) {
            estado = "ACTIVA";
        }

        if (monto == null) {
            monto = 0.0;
        }

        if (cantidad == null) {
            cantidad = 0;
        }
    }


    // ==================================================
    // PRE UPDATE
    // ==================================================

    @PreUpdate
    public void preUpdate() {

        if (estado == null || estado.isBlank()) {
            estado = "ACTIVA";
        }

        if (monto == null) {
            monto = 0.0;
        }

        if (cantidad == null) {
            cantidad = 0;
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


    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }


    public DetalleVenta getDetalleVenta() {
        return detalleVenta;
    }

    public void setDetalleVenta(DetalleVenta detalleVenta) {
        this.detalleVenta = detalleVenta;
    }


    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }


    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }


    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }


    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }


    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }


    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(
            LocalDateTime fechaDevolucion
    ) {
        this.fechaDevolucion = fechaDevolucion;
    }


    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

