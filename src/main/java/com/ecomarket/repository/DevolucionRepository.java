package com.ecomarket.repository;

import com.ecomarket.model.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ======================================================
 * ↩️ REPOSITORY DEVOLUCION
 * ======================================================
 *
 * Este repository trabaja con devoluciones.
 *
 * IMPORTANTE:
 *
 * Devolucion no tiene actualmente un campo negocioId.
 *
 * Por eso la seguridad por negocio se realiza a través
 * de la Venta relacionada:
 *
 * Devolucion
 *      ↓
 * Venta
 *      ↓
 * negocioId
 *
 * De esta manera un negocio solamente puede consultar
 * devoluciones de sus propias ventas.
 *
 * ======================================================
 */
@Repository
public interface DevolucionRepository
        extends JpaRepository<Devolucion, Integer> {


    // ==================================================
    // 🔍 DEVOLUCIÓN POR ID + NEGOCIO
    // ==================================================
    //
    // Comprueba que la devolución pertenezca a una venta
    // cuyo negocioId sea el negocio del usuario autenticado.
    //
    // ==================================================

    @Query("""
        SELECT d
        FROM Devolucion d
        WHERE d.id = :id
        AND d.venta.negocioId = :negocioId
    """)
    Optional<Devolucion> findByIdAndNegocioId(
            @Param("id") Integer id,
            @Param("negocioId") Integer negocioId
    );


    // ==================================================
    // ↩️ DEVOLUCIONES DE UNA VENTA + NEGOCIO
    // ==================================================

    @Query("""
        SELECT d
        FROM Devolucion d
        WHERE d.venta.id = :ventaId
        AND d.venta.negocioId = :negocioId
        ORDER BY d.fechaDevolucion DESC
    """)
    List<Devolucion> findByVentaIdAndNegocioId(
            @Param("ventaId") Integer ventaId,
            @Param("negocioId") Integer negocioId
    );


    // ==================================================
    // 📦 DEVOLUCIONES DE UN PRODUCTO + NEGOCIO
    // ==================================================

    @Query("""
        SELECT d
        FROM Devolucion d
        WHERE d.producto.id = :productoId
        AND d.venta.negocioId = :negocioId
        ORDER BY d.fechaDevolucion DESC
    """)
    List<Devolucion> findByProductoIdAndNegocioId(
            @Param("productoId") Integer productoId,
            @Param("negocioId") Integer negocioId
    );


    // ==================================================
    // 🔎 DEVOLUCIONES POR ESTADO + NEGOCIO
    // ==================================================

    @Query("""
        SELECT d
        FROM Devolucion d
        WHERE UPPER(d.estado) = UPPER(:estado)
        AND d.venta.negocioId = :negocioId
        ORDER BY d.fechaDevolucion DESC
    """)
    List<Devolucion> findByEstadoAndNegocioId(
            @Param("estado") String estado,
            @Param("negocioId") Integer negocioId
    );


    // ==================================================
    // 📋 TODAS LAS DEVOLUCIONES DE UN NEGOCIO
    // ==================================================

    @Query("""
        SELECT d
        FROM Devolucion d
        WHERE d.venta.negocioId = :negocioId
        ORDER BY d.fechaDevolucion DESC
    """)
    List<Devolucion> findAllByNegocioId(
            @Param("negocioId") Integer negocioId
    );
}

