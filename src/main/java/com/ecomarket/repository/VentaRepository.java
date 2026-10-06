package com.ecomarket.repository;

import com.ecomarket.model.Venta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ======================================================
 * 🧾 REPOSITORY VENTA
 * ======================================================
 *
 * Este repository contiene las consultas relacionadas
 * con las ventas de EcoMarket PRO.
 *
 * ======================================================
 *
 * 🔐 SEGURIDAD MULTI-NEGOCIO
 *
 * Todas las consultas que muestran información de ventas
 * deben trabajar utilizando:
 *
 *              negocioId
 *
 * El negocioId NO debe venir desde el HTML ni desde
 * JavaScript.
 *
 * Se obtiene desde:
 *
 * Usuario autenticado
 *        ↓
 * CustomUserDetails
 *        ↓
 * getNegocioId()
 *
 * De esta manera:
 *
 * NEGOCIO 1
 *   ↓
 * solamente puede consultar ventas del NEGOCIO 1
 *
 * NEGOCIO 2
 *   ↓
 * solamente puede consultar ventas del NEGOCIO 2
 *
 * ======================================================
 */
@Repository
public interface VentaRepository
        extends JpaRepository<Venta, Integer> {


    // ======================================================
    // 💰 VENTAS DEL DÍA POR NEGOCIO
    // ======================================================
    //
    // Calcula solamente las ventas realizadas hoy
    // pertenecientes al negocio indicado.
    //
    // ======================================================

    @Query("""
        SELECT COALESCE(SUM(v.total), 0)
        FROM Venta v
        WHERE DATE(v.fecha) = CURRENT_DATE
        AND v.negocioId = :negocioId
    """)
    Double totalVentasHoy(
            @Param("negocioId") Integer negocioId
    );


    // ======================================================
    // 💰 VENTAS DEL MES POR NEGOCIO
    // ======================================================
    //
    // Calcula las ventas del mes actual únicamente
    // para el negocio autenticado.
    //
    // ======================================================

    @Query("""
        SELECT COALESCE(SUM(v.total), 0)
        FROM Venta v
        WHERE MONTH(v.fecha) = MONTH(CURRENT_DATE)
        AND YEAR(v.fecha) = YEAR(CURRENT_DATE)
        AND v.negocioId = :negocioId
    """)
    Double totalVentasMes(
            @Param("negocioId") Integer negocioId
    );


    // ======================================================
    // 🧾 ÚLTIMAS 10 VENTAS DEL NEGOCIO
    // ======================================================
    //
    // IMPORTANTE:
    //
    // No usamos:
    //
    // findTop10ByOrderByFechaDesc()
    //
    // porque devolvería ventas de TODOS los negocios.
    //
    // ======================================================

    List<Venta> findTop10ByNegocioIdOrderByFechaDesc(
            Integer negocioId
    );


    // ======================================================
    // 🔍 VENTA POR ID + NEGOCIO
    // ======================================================
    //
    // Esta consulta es MUY importante para la seguridad.
    //
    // Ejemplo:
    //
    // Negocio 2 solicita:
    //
    // /api/ventas/1
    //
    // Si la venta 1 pertenece al Negocio 1,
    // la consulta NO la devuelve.
    //
    // ======================================================

    Optional<Venta> findByIdAndNegocioId(
            Integer id,
            Integer negocioId
    );


    // ======================================================
    // 💳 FACTURAS A CRÉDITO DE UN CLIENTE
    // POR NEGOCIO
    // ======================================================
    //
    // También verificamos el negocio.
    //
    // Esto evita que un cliente pueda terminar mostrando
    // ventas a crédito pertenecientes a otro negocio.
    //
    // ======================================================

    @Query("""
        SELECT v
        FROM Venta v
        WHERE v.idCliente = :idCliente
        AND v.negocioId = :negocioId
        AND UPPER(v.metodoPago) = 'DEUDA'
        AND UPPER(v.estado) = 'COMPLETADA'
        ORDER BY v.fecha DESC
    """)
    List<Venta> findFacturasDeudaPorCliente(
            @Param("idCliente") Integer idCliente,
            @Param("negocioId") Integer negocioId
    );


    // ======================================================
    // 📋 TODAS LAS VENTAS DE UN NEGOCIO
    // ======================================================
    //
    // Solamente devuelve las ventas pertenecientes
    // al negocio autenticado.
    //
    // ======================================================

    List<Venta> findByNegocioIdOrderByFechaDesc(
            Integer negocioId
    );
}

