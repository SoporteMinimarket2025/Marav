// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.dto;

/**
 * ======================================================
 * 👥 DTO CLIENTE
 * ======================================================
 */
public class ClienteDTO {

    private String nombre;

    private String tipoIdentificacion;

    private String numeroIdentificacion;

    private String telefono;

    private String direccion;

    // ==================================================
    // GETTERS & SETTERS
    // ==================================================

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(
            String tipoIdentificacion
    ) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(
            String numeroIdentificacion
    ) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}