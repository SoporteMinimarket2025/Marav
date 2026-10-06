package com.ecomarket.repository;

import com.ecomarket.model.DetalleVenta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * ======================================================
 * 📦 REPOSITORIO DETALLE VENTA
 * ======================================================
 *
 * Gestiona los detalles asociados a las ventas.
 *
 * ======================================================
 *
 * 🔐 SEGURIDAD MULTI-NEGOCIO
 *
 * DetalleVenta actualmente no tiene negocioId propio.
 *
 * Por eso la seguridad se realiza mediante la Venta:
 *
 * DetalleVenta
 *      ↓
 * idVenta
 *      ↓
 * Venta
 *      ↓
 * negocioId
 *
 * De esta manera un detalle solamente puede ser consultado
 * cuando la venta pertenece al negocio autenticado.
 *
 * ======================================================
 */
public interface DetalleVentaRepository
        extends JpaRepository<DetalleVenta, Integer> {


    // ======================================================
    // 🔍 DETALLES DE UNA VENTA
    // ======================================================
    //
    // ⚠️ Método existente.
    //
    // Se conserva para no romper código anterior.
    //
    // Para operaciones expuestas por la aplicación,
    // debemos preferir:
    //
    // findByIdVentaAndNegocioId(...)
    //
    // ======================================================

    List<DetalleVenta> findByIdVenta(
            Integer idVenta
    );


    // ======================================================
    // 🔐 DETALLES DE UNA VENTA + NEGOCIO
    // ======================================================
    //
    // Verifica:
    //
    // 1. Que el detalle pertenezca a la venta indicada.
    //
    // 2. Que esa venta pertenezca al negocio autenticado.
    //
    // ======================================================

    @Query("""
        SELECT d
        FROM DetalleVenta d
        WHERE d.idVenta = :ventaId
        AND EXISTS (
            SELECT v.id
            FROM Venta v
            WHERE v.id = d.idVenta
            AND v.negocioId = :negocioId
        )
    """)
    List<DetalleVenta> findByIdVentaAndNegocioId(
            @Param("ventaId") Integer ventaId,
            @Param("negocioId") Integer negocioId
    );


    // ======================================================
    // 🔐 DETALLE POR ID + NEGOCIO
    // ======================================================
    //
    // Ejemplo:
    //
    // Negocio 2 solicita:
    //
    // detalle ID = 15
    //
    // Si ese detalle pertenece a una venta del Negocio 1,
    // no será devuelto.
    //
    // ======================================================

    @Query("""
        SELECT d
        FROM DetalleVenta d
        WHERE d.id = :id
        AND EXISTS (
            SELECT v.id
            FROM Venta v
            WHERE v.id = d.idVenta
            AND v.negocioId = :negocioId
        )
    """)
    Optional<DetalleVenta> findByIdAndNegocioId(
            @Param("id") Integer id,
            @Param("negocioId") Integer negocioId
    );
}