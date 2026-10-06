
// ======================================================
// 📁 PACKAGE
// ======================================================
        package com.ecomarket.repository;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ======================================================
 * 📦 REPOSITORY PRODUCTOS
 * ======================================================
 *
 * ✔ CRUD automático
 * ✔ Listado por negocio
 * ✔ Búsqueda por nombre
 * ✔ Búsqueda segura por ID + negocio
 *
 * ======================================================
 */
@Repository
public interface ProductoRepository
        extends JpaRepository<Producto, Integer> {

    // =====================================================
    // 📋 LISTAR PRODUCTOS ACTIVOS
    // =====================================================
    List<Producto> findByNegocioIdAndEstadoTrue(
            Integer negocioId
    );


    // =====================================================
    // 🔍 BUSCAR PRODUCTOS POR NOMBRE
    // =====================================================
    List<Producto>
    findByNegocioIdAndEstadoTrueAndNombreContainingIgnoreCase(
            Integer negocioId,
            String nombre
    );


    // =====================================================
    // 🔐 BUSCAR PRODUCTO POR ID + NEGOCIO
    // =====================================================
    //
    // Esto evita que un negocio pueda acceder a un
    // producto perteneciente a otro negocio.
    //
    Optional<Producto> findByIdAndNegocioId(
            Integer id,
            Integer negocioId
    );


    // =====================================================
    // 🔢 CONTAR PRODUCTOS ACTIVOS POR NEGOCIO
    // =====================================================
    Long countByNegocioIdAndEstadoTrue(
            Integer negocioId
    );
}



