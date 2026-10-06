package com.ecomarket.repository;

import com.ecomarket.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * ======================================================
 * 🧾 REPOSITORY FACTURA
 * ======================================================
 *
 * Factura no necesita tener un negocioId propio porque
 * pertenece a una Venta.
 *
 * Relación:
 *
 * Factura
 *    ↓
 * ventaId
 *    ↓
 * Venta
 *    ↓
 * negocioId
 *
 * Por lo tanto todas las consultas importantes verifican
 * el negocio a través de Venta.
 *
 * ======================================================
 */
public interface FacturaRepository
        extends JpaRepository<Factura, Integer> {


    // ==================================================
    // 🔍 FACTURA DE UNA VENTA + NEGOCIO
    // ==================================================

    @Query("""
        SELECT f
        FROM Factura f
        WHERE f.ventaId = :ventaId
        AND EXISTS (
            SELECT v.id
            FROM Venta v
            WHERE v.id = f.ventaId
            AND v.negocioId = :negocioId
        )
    """)
    Optional<Factura> findByVentaIdAndNegocioId(
            @Param("ventaId") Integer ventaId,
            @Param("negocioId") Integer negocioId
    );


    // ==================================================
    // 📅 TODAS LAS FACTURAS DE UN NEGOCIO
    // ==================================================

    @Query("""
        SELECT f
        FROM Factura f
        WHERE EXISTS (
            SELECT v.id
            FROM Venta v
            WHERE v.id = f.ventaId
            AND v.negocioId = :negocioId
        )
        ORDER BY f.fecha DESC
    """)
    List<Factura> findAllByNegocioIdOrderByFechaDesc(
            @Param("negocioId") Integer negocioId
    );


    // ==================================================
    // 🔎 BUSCAR FACTURA POR NÚMERO + NEGOCIO
    // ==================================================

    @Query("""
        SELECT f
        FROM Factura f
        WHERE LOWER(f.numeroFactura)
              LIKE LOWER(CONCAT('%', :numeroFactura, '%'))
        AND EXISTS (
            SELECT v.id
            FROM Venta v
            WHERE v.id = f.ventaId
            AND v.negocioId = :negocioId
        )
        ORDER BY f.fecha DESC
    """)
    List<Factura> findByNumeroFacturaContainingIgnoreCaseAndNegocioId(
            @Param("numeroFactura") String numeroFactura,
            @Param("negocioId") Integer negocioId
    );
}