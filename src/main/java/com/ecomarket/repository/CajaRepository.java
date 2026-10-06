// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.repository;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Caja;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ======================================================
 * 💰 REPOSITORY CAJA
 * ======================================================
 *
 * Todas las consultas principales están filtradas por
 * negocio.
 *
 * La relación se realiza mediante:
 *
 * Caja.idUsuario
 *      ↓
 * Usuario.id
 *      ↓
 * Usuario.negocioId
 *
 * ======================================================
 */
@Repository
public interface CajaRepository
        extends JpaRepository<Caja, Integer> {


    // ==================================================
    // 📋 LISTAR CAJAS DEL NEGOCIO
    // ==================================================
    //
    // Solo devuelve cajas cuyo usuario pertenece al
    // negocio indicado.
    //
    // ==================================================

    @Query("""
        SELECT c
        FROM Caja c
        WHERE EXISTS (
            SELECT u.id
            FROM Usuario u
            WHERE u.id = c.idUsuario
            AND u.negocioId = :negocioId
        )
        ORDER BY c.fechaApertura DESC
    """)
    List<Caja> findAllByNegocioIdOrderByFechaAperturaDesc(
            @Param("negocioId") Integer negocioId
    );


    // ==================================================
    // 🔎 BUSCAR UNA CAJA DEL NEGOCIO
    // ==================================================

    @Query("""
        SELECT c
        FROM Caja c
        WHERE c.id = :id
        AND EXISTS (
            SELECT u.id
            FROM Usuario u
            WHERE u.id = c.idUsuario
            AND u.negocioId = :negocioId
        )
    """)
    Optional<Caja> findByIdAndNegocioId(
            @Param("id") Integer id,
            @Param("negocioId") Integer negocioId
    );


    // ==================================================
    // 💰 BUSCAR CAJA ABIERTA DEL NEGOCIO
    // ==================================================
    //
    // IMPORTANTE:
    //
    // Antes buscábamos una caja abierta globalmente.
    //
    // Ahora cada negocio puede tener su propia caja
    // abierta.
    //
    // ==================================================

    @Query("""
        SELECT c
        FROM Caja c
        WHERE UPPER(c.estado) = 'ABIERTA'
        AND EXISTS (
            SELECT u.id
            FROM Usuario u
            WHERE u.id = c.idUsuario
            AND u.negocioId = :negocioId
        )
        ORDER BY c.fechaApertura DESC
    """)
    List<Caja> findCajasAbiertasByNegocioId(
            @Param("negocioId") Integer negocioId
    );


    // ==================================================
    // 💰 OBTENER LA ÚLTIMA CAJA ABIERTA DEL NEGOCIO
    // ==================================================

    @Query("""
        SELECT c
        FROM Caja c
        WHERE UPPER(c.estado) = 'ABIERTA'
        AND EXISTS (
            SELECT u.id
            FROM Usuario u
            WHERE u.id = c.idUsuario
            AND u.negocioId = :negocioId
        )
        ORDER BY c.fechaApertura DESC
    """)
    List<Caja> buscarCajaAbierta(
            @Param("negocioId") Integer negocioId
    );
}