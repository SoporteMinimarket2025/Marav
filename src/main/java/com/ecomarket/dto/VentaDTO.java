// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.dto;

// ======================================================
// 📚 IMPORTS
// ======================================================
import java.util.List;

/**
 * ======================================================
 * 📥 DTO PARA CREAR VENTAS
 * ======================================================
 *
 * ✔ Productos
 * ✔ Cliente
 * ✔ Métodos de pago
 * ✔ WhatsApp
 * ✔ Facturación
 * ✔ IVA opcional
 *
 * ======================================================
 */
public class VentaDTO {

    // ==================================================
    // 🏪 NEGOCIO
    // ==================================================
    private Integer negocioId;

    // ==================================================
    // 👤 USUARIO
    // ==================================================
    private Integer usuarioId;

    // ==================================================
    // 👥 CLIENTE
    // ==================================================
    private Integer clienteId;

    // ==================================================
    // 💳 MÉTODO PAGO
    // ==================================================
    private String metodoPago;

    // ==================================================
    // 💵 EFECTIVO RECIBIDO
    // ==================================================
    private Double efectivoRecibido;

    // ==================================================
    // 📲 ENVIAR WHATSAPP
    // ==================================================
    private Boolean enviarWhatsapp;

    // ==================================================
    // 📞 TELÉFONO ENVÍO
    // ==================================================
    private String telefonoEnvio;

    // ==================================================
    // 🧾 FACTURACIÓN
    // ==================================================

    // ✅ aplicar IVA
    private Boolean aplicarIva;

    // ✅ porcentaje IVA
    private Double porcentajeIva;

    // ==================================================
    // 📦 DETALLES
    // ==================================================
    private List<DetalleDTO> detalles;

    // ==================================================
    // 📦 DETALLE INTERNO
    // ==================================================
    public static class DetalleDTO {

        // 🔹 producto
        private Integer productoId;

        // 🔹 cantidad
        private Integer cantidad;

        // 🔹 precio
        private Double precio;

        // ==============================================
        // GETTERS & SETTERS
        // ==============================================

        public Integer getProductoId() {
            return productoId;
        }

        public void setProductoId(Integer productoId) {
            this.productoId = productoId;
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
    }

    // ==================================================
    // GETTERS & SETTERS PRINCIPALES
    // ==================================================

    public Integer getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Integer negocioId) {
        this.negocioId = negocioId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Double getEfectivoRecibido() {
        return efectivoRecibido;
    }

    public void setEfectivoRecibido(Double efectivoRecibido) {
        this.efectivoRecibido = efectivoRecibido;
    }

    public Boolean getEnviarWhatsapp() {
        return enviarWhatsapp;
    }

    public void setEnviarWhatsapp(Boolean enviarWhatsapp) {
        this.enviarWhatsapp = enviarWhatsapp;
    }

    public String getTelefonoEnvio() {
        return telefonoEnvio;
    }

    public void setTelefonoEnvio(String telefonoEnvio) {
        this.telefonoEnvio = telefonoEnvio;
    }

    public Boolean getAplicarIva() {
        return aplicarIva;
    }

    public void setAplicarIva(Boolean aplicarIva) {
        this.aplicarIva = aplicarIva;
    }

    public Double getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(Double porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public List<DetalleDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleDTO> detalles) {
        this.detalles = detalles;
    }
}

