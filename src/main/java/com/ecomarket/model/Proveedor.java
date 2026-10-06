package com.ecomarket.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * ============================================================
 * ENTIDAD PROVEEDOR
 * ============================================================
 *
 * Representa un proveedor registrado dentro de EcoMarket PRO.
 *
 * IMPORTANTE:
 *
 * EcoMarket PRO permite manejar varios negocios.
 *
 * Por eso cada proveedor debe quedar asociado a un negocio
 * mediante el campo negocioId.
 *
 * Ejemplo:
 *
 * Negocio 1
 *   ├── Proveedor A
 *   └── Proveedor B
 *
 * Negocio 2
 *   ├── Proveedor C
 *   └── Proveedor D
 *
 * Un negocio solamente debe poder consultar y administrar
 * sus propios proveedores.
 * ============================================================
 */
@Entity
@Table(name = "proveedores")
public class Proveedor {

    // =========================================================
    // ID DEL PROVEEDOR
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    // =========================================================
    // INFORMACIÓN DEL PROVEEDOR
    // =========================================================

    private String nombre;

    private String nit;

    private String telefono;

    private String email;

    private String direccion;

    private String contacto;

    private String estado;


    // =========================================================
    // NEGOCIO AL QUE PERTENECE
    // =========================================================

    /**
     * Identificador del negocio propietario del proveedor.
     *
     * Este campo es fundamental para el aislamiento
     * de información entre negocios.
     *
     * El valor NO debe venir del formulario.
     *
     * Se asigna desde el usuario autenticado mediante:
     *
     * CustomUserDetails.getNegocioId()
     */
    @Column(name = "negocio_id")
    private Integer negocioId;


    // =========================================================
    // FECHA DE REGISTRO
    // =========================================================

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;


    // =========================================================
    // PRE-PERSIST
    // =========================================================

    /**
     * Valores iniciales antes de guardar un proveedor.
     */
    @PrePersist
    public void prePersist() {

        // Fecha automática de registro
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }

        // Estado inicial
        if (estado == null || estado.isBlank()) {
            estado = "ACTIVO";
        }
    }


    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

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


    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }


    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }


    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }


    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


    // =========================================================
    // GETTER Y SETTER DE NEGOCIO
    // =========================================================

    public Integer getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Integer negocioId) {
        this.negocioId = negocioId;
    }


    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(
            LocalDateTime fechaRegistro
    ) {
        this.fechaRegistro = fechaRegistro;
    }
}