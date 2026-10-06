
// ======================================================
// 📁 PACKAGE
// ======================================================
        package com.ecomarket.repository;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Inventario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ======================================================
 * 📦 REPOSITORY INVENTARIO
 * ======================================================
 *
 * 🔐 El inventario se filtra mediante el negocio del
 * producto relacionado.
 *
 * ======================================================
 */
@Repository
public interface InventarioRepository
        extends JpaRepository<Inventario, Integer> {


    // =====================================================
    // 📋 LISTAR INVENTARIO DE UN NEGOCIO
    // =====================================================
    //
    // Inventario no tiene negocioId directamente.
    //
    // Por eso buscamos los inventarios cuyo producto
    // pertenece al negocio indicado.
    //
    @Query("""
        SELECT i
        FROM Inventario i
        WHERE EXISTS (
            SELECT p.id
            FROM Producto p
            WHERE p.id = i.productoId
            AND p.negocioId = :negocioId
        )
    """)
    List<Inventario> findAllByNegocioId(
            @Param("negocioId") Integer negocioId
    );


    // =====================================================
    // 🔍 BUSCAR INVENTARIO POR ID + NEGOCIO
    // =====================================================
    @Query("""
        SELECT i
        FROM Inventario i
        WHERE i.id = :id
        AND EXISTS (
            SELECT p.id
            FROM Producto p
            WHERE p.id = i.productoId
            AND p.negocioId = :negocioId
        )
    """)
    Optional<Inventario> findByIdAndNegocioId(
            @Param("id") Integer id,
            @Param("negocioId") Integer negocioId
    );


    // =====================================================
    // 📉 STOCK BAJO POR NEGOCIO
    // =====================================================
    @Query("""
        SELECT i
        FROM Inventario i
        WHERE i.stockActual <= i.stockMinimo
        AND EXISTS (
            SELECT p.id
            FROM Producto p
            WHERE p.id = i.productoId
            AND p.negocioId = :negocioId
        )
    """)
    List<Inventario> obtenerStockBajo(
            @Param("negocioId") Integer negocioId
    );


    // =====================================================
    // 🔍 INVENTARIO POR PRODUCTO
    // =====================================================
    Optional<Inventario> findByProductoId(
            Integer productoId
    );


    // =====================================================
    // 🔍 INVENTARIO POR PRODUCTO + NEGOCIO
    // =====================================================
    @Query("""
        SELECT i
        FROM Inventario i
        WHERE i.productoId = :productoId
        AND EXISTS (
            SELECT p.id
            FROM Producto p
            WHERE p.id = i.productoId
            AND p.negocioId = :negocioId
        )
    """)
    Optional<Inventario> findByProductoIdAndNegocioId(
            @Param("productoId") Integer productoId,
            @Param("negocioId") Integer negocioId
    );


    // =====================================================
    // 🔎 EXISTE INVENTARIO POR PRODUCTO
    // =====================================================
    boolean existsByProductoId(
            Integer productoId
    );


    // =====================================================
    // 🔐 EXISTE INVENTARIO POR PRODUCTO + NEGOCIO
    // =====================================================
    @Query("""
        SELECT COUNT(i) > 0
        FROM Inventario i
        WHERE i.productoId = :productoId
        AND EXISTS (
            SELECT p.id
            FROM Producto p
            WHERE p.id = i.productoId
            AND p.negocioId = :negocioId
        )
    """)
    boolean existsByProductoIdAndNegocioId(
            @Param("productoId") Integer productoId,
            @Param("negocioId") Integer negocioId
    );
}
