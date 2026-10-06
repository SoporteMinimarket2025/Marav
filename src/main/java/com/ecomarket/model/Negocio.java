package com.ecomarket.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 🏪 ENTIDAD NEGOCIO
 */
@Data
@Entity
@Table(name = "negocios")
public class Negocio {

    // 🔑 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 🏪 Nombre del negocio
    @Column(nullable = false)
    private String nombre;

    // 📍 Dirección
    private String direccion;

    // 📞 Teléfono
    private String telefono;

    // 📅 Fecha creación
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // 🔥 Antes de guardar
    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
    }
}

