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
 * 🧾 ENTIDAD VENTA
 * ======================================================
 *
 * Representa una venta/factura realizada
 * en el sistema EcoMarket POS.
 *
 * ✔ Control de ventas
 * ✔ Métodos de pago
 * ✔ Fiado / deuda
 * ✔ WhatsApp
 * ✔ Facturación
 * ✔ Historial
 * ✔ Reportes
 *
 * ======================================================
 */
@Entity
@Table(name = "ventas")
public class Venta {

    // ==================================================
    // 🔹 ID AUTOINCREMENTAL
    // ==================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ==================================================
    // 🧾 NÚMERO FACTURA
    // ==================================================
    @Column(name = "numero_factura",
            unique = true,
            length = 30)
    private String numeroFactura;

    // ==================================================
    // 📅 FECHA VENTA
    // ==================================================
    private LocalDateTime fecha;

    // ==================================================
    // 👤 USUARIO QUE VENDE
    // ==================================================
    @Column(name = "id_usuario",
            nullable = false)
    private Integer idUsuario;

    // ==================================================
    // 👥 CLIENTE
    // OPCIONAL
    // ==================================================
    @Column(name = "id_cliente")
    private Integer idCliente;

    // ======================================================
// 🏪 NEGOCIO AL QUE PERTENECE LA VENTA
// ======================================================
//
// 🔐 Fundamental para aislamiento multi-negocio.
//
// Cada venta pertenece a un único negocio.
//
// ======================================================
    @Column(name = "negocio_id")
    private Integer negocioId;
    // ==================================================
    // 💳 MÉTODO PAGO
    //
    // EFECTIVO
    // NEQUI
    // DAVIPLATA
    // TRANSFERENCIA
    // DEUDA
    // ==================================================
    @Column(name = "metodo_pago",
            length = 30)
    private String metodoPago;

    // ==================================================
    // 💰 TOTAL VENTA
    // ==================================================
    @Column(nullable = false)
    private Double total = 0.0;

    // ==================================================
    // 💵 EFECTIVO RECIBIDO
    // ==================================================
    @Column(name = "efectivo_recibido")
    private Double efectivoRecibido = 0.0;

    // ==================================================
    // 🔁 CAMBIO
    // ==================================================
    private Double cambio = 0.0;

    // ==================================================
    // 📲 ENVIAR FACTURA WHATSAPP
    // ==================================================
    @Column(name = "enviar_whatsapp")
    private Boolean enviarWhatsapp = false;

    // ==================================================
    // 📞 TELÉFONO DESTINO
    // ==================================================
    @Column(name = "telefono_envio",
            length = 20)
    private String telefonoEnvio;

    // ==================================================
    // 📦 ESTADO VENTA
    //
    // COMPLETADA
    // PENDIENTE
    // ANULADA
    // ==================================================
    @Column(length = 20)
    private String estado = "COMPLETADA";

    // ==================================================
    // 🔥 MÉTODO AUTOMÁTICO
    // ==================================================
    @PrePersist
    public void prePersist() {

        // ==============================================
        // 📅 FECHA AUTOMÁTICA
        // ==============================================
        if (fecha == null) {

            fecha = LocalDateTime.now();
        }

        // ==============================================
        // 📦 ESTADO POR DEFECTO
        // ==============================================
        if (estado == null
                || estado.isBlank()) {

            estado = "COMPLETADA";
        }

        // ==============================================
        // 📲 WHATSAPP
        // ==============================================
        if (enviarWhatsapp == null) {

            enviarWhatsapp = false;
        }

        // ==============================================
        // 💰 TOTAL
        // ==============================================
        if (total == null) {

            total = 0.0;
        }

        // ==============================================
        // 💵 EFECTIVO
        // ==============================================
        if (efectivoRecibido == null) {

            efectivoRecibido = 0.0;
        }

        // ==============================================
        // 🔁 CAMBIO
        // ==============================================
        if (cambio == null) {

            cambio = 0.0;
        }

        // ==============================================
        // 🧾 GENERAR FACTURA
        // ==============================================
        if (numeroFactura == null
                || numeroFactura.isBlank()) {

            numeroFactura =
                    "FAC-" + System.currentTimeMillis();
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

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(
            String numeroFactura
    ) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(
            LocalDateTime fecha
    ) {
        this.fecha = fecha;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(
            Integer idUsuario
    ) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(
            Integer idCliente
    ) {
        this.idCliente = idCliente;
    }
    public Integer getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Integer negocioId) {
        this.negocioId = negocioId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(
            String metodoPago
    ) {
        this.metodoPago = metodoPago;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(
            Double total
    ) {
        this.total = total;
    }

    public Double getEfectivoRecibido() {
        return efectivoRecibido;
    }

    public void setEfectivoRecibido(
            Double efectivoRecibido
    ) {
        this.efectivoRecibido = efectivoRecibido;
    }

    public Double getCambio() {
        return cambio;
    }

    public void setCambio(
            Double cambio
    ) {
        this.cambio = cambio;
    }

    public Boolean getEnviarWhatsapp() {
        return enviarWhatsapp;
    }

    public void setEnviarWhatsapp(
            Boolean enviarWhatsapp
    ) {
        this.enviarWhatsapp = enviarWhatsapp;
    }

    public String getTelefonoEnvio() {
        return telefonoEnvio;
    }

    public void setTelefonoEnvio(
            String telefonoEnvio
    ) {
        this.telefonoEnvio = telefonoEnvio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(
            String estado
    ) {
        this.estado = estado;
    }
}

