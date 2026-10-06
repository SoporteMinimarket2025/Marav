package com.ecomarket.dto;

/**
 * 📥 DTO PARA REGISTRO
 * Recibe datos del frontend
 */
public class RegistroDTO {

    // 🏪 Nombre del negocio
    private String nombreNegocio;

    // 👤 Nombre del usuario
    private String nombre;

    // 📧 Email (login)
    private String email;

    // 🔑 Password
    private String password;

    // ======================================================
    // 🔹 GETTERS Y SETTERS
    // ======================================================

    public String getNombreNegocio() {
        return nombreNegocio;
    }

    public void setNombreNegocio(String nombreNegocio) {
        this.nombreNegocio = nombreNegocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}