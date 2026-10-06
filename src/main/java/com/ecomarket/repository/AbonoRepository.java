package com.ecomarket.repository;

import com.ecomarket.model.Abono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * 💰 ABONO REPOSITORY | ECOMARKET PRO
 * ============================================================
 *
 * Acceso a la tabla "abonos".
 *
 * ============================================================
 * 🔐 SEGURIDAD MULTI-NEGOCIO
 * ============================================================
 *
 * Abono no tiene negocioId directamente.
 *
 * La relación es:
 *
 *      Abono
 *        ↓
 *      idVenta
 *        ↓
 *      Venta
 *        ↓
 *      negocioId
 *
 * Por esta razón todas las consultas importantes verifican
 * el negocio mediante la Venta relacionada.
 *
 * ============================================================
 */
@Repository
public interface AbonoRepository extends JpaRepository<Abono, Integer> {


    // ========================================================
    // 📋 LISTAR ABONOS DE UN NEGOCIO
    // ========================================================

    @Query("""
        SELECT a
        FROM Abono a
        WHERE a.idVenta IN (
            SELECT v.id
            FROM Venta v
            WHERE v.negocioId = :negocioId
        )
        ORDER BY a.fecha DESC
    """)
    List<Abono> findAllByNegocioIdOrderByFechaDesc(
            @Param("negocioId") Integer negocioId
    );


    // ========================================================
    // 🔍 BUSCAR ABONO POR ID + NEGOCIO
    // ========================================================

    @Query("""
        SELECT a
        FROM Abono a
        WHERE a.id = :id
        AND a.idVenta IN (
            SELECT v.id
            FROM Venta v
            WHERE v.negocioId = :negocioId
        )
    """)
    Optional<Abono> findByIdAndNegocioId(
            @Param("id") Integer id,
            @Param("negocioId") Integer negocioId
    );


    // ========================================================
    // 👤 ABONOS DE UN CLIENTE + NEGOCIO
    // ========================================================

    @Query("""
        SELECT a
        FROM Abono a
        WHERE a.idCliente = :idCliente
        AND a.idVenta IN (
            SELECT v.id
            FROM Venta v
            WHERE v.negocioId = :negocioId
        )
        ORDER BY a.fecha DESC
    """)
    List<Abono> findByIdClienteAndNegocioIdOrderByFechaDesc(
            @Param("idCliente") Integer idCliente,
            @Param("negocioId") Integer negocioId
    );


    // ========================================================
    // 🧾 ABONOS DE UNA FACTURA + NEGOCIO
    // ========================================================

    @Query("""
        SELECT a
        FROM Abono a
        WHERE a.idVenta = :idVenta
        AND EXISTS (
            SELECT v.id
            FROM Venta v
            WHERE v.id = a.idVenta
            AND v.negocioId = :negocioId
        )
        ORDER BY a.fecha DESC
    """)
    List<Abono> findByIdVentaAndNegocioIdOrderByFechaDesc(
            @Param("idVenta") Integer idVenta,
            @Param("negocioId") Integer negocioId
    );
}