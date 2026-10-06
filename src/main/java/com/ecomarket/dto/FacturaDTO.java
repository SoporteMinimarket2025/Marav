package com.ecomarket.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FacturaDTO {

    // ==================================================
    // 🧾 ID FACTURA
    // ==================================================
    private Integer id;

    // ==================================================
    // 🧾 DATOS FACTURA
    // ==================================================
    private Integer ventaId;

    private String numeroFactura;

    private LocalDateTime fecha;

    private String metodoPago;

    private String estado;

    // ==================================================
    // 👤 CLIENTE
    // ==================================================
    private String clienteNombre;

    private String clienteTelefono;

    // ==================================================
    // 💰 TOTALES
    // ==================================================
    private Double subtotal;

    private Double iva;

    private Boolean aplicarIva;

    private Double total;

    // ==================================================
    // ↩️ DEVOLUCIONES
    // ==================================================
    private Double totalDevuelto;

    private Double totalNeto;

    private Double efectivoRecibido;

    private Double cambio;

    // ==================================================
    // 📦 DETALLES
    // ==================================================
    private List<DetalleFacturaDTO> detalles;

    public FacturaDTO() {
    }

    // ==================================================
    // 📦 DETALLE FACTURA
    // ==================================================
    public static class DetalleFacturaDTO {

        private String producto;

        private Integer cantidad;

        private Double precio;

        private Double subtotal;

        public DetalleFacturaDTO() {
        }

        public String getProducto() {
            return producto;
        }

        public void setProducto(String producto) {
            this.producto = producto;
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

    // ==================================================
    // GETTERS Y SETTERS
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    public void setClienteTelefono(String clienteTelefono) {
        this.clienteTelefono = clienteTelefono;
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

    public Double getTotalDevuelto() {
        return totalDevuelto;
    }

    public void setTotalDevuelto(Double totalDevuelto) {
        this.totalDevuelto = totalDevuelto;
    }

    public Double getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(Double totalNeto) {
        this.totalNeto = totalNeto;
    }

    public Double getEfectivoRecibido() {
        return efectivoRecibido;
    }

    public void setEfectivoRecibido(Double efectivoRecibido) {
        this.efectivoRecibido = efectivoRecibido;
    }

    public Double getCambio() {
        return cambio;
    }

    public void setCambio(Double cambio) {
        this.cambio = cambio;
    }

    public List<DetalleFacturaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaDTO> detalles) {
        this.detalles = detalles;
    }
}

