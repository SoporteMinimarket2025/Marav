
// ======================================================
// 📁 PACKAGE
// ======================================================
        package com.ecomarket.repository;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ======================================================
 * 👥 REPOSITORY CLIENTES
 * ======================================================
 *
 * 🔐 Todos los métodos importantes utilizan negocioId.
 *
 * Esto evita que un negocio pueda consultar clientes
 * pertenecientes a otro negocio.
 *
 * ======================================================
 */
@Repository
public interface ClienteRepository
        extends JpaRepository<Cliente, Integer> {

    // ==================================================
    // 📋 CLIENTES ACTIVOS DE UN NEGOCIO
    // ==================================================
    List<Cliente> findByNegocioIdAndEstadoTrue(
            Integer negocioId
    );

    // ==================================================
    // 🔍 BUSCAR CLIENTES POR NOMBRE
    // ==================================================
    List<Cliente>
    findByNegocioIdAndNombreContainingIgnoreCaseAndEstadoTrue(
            Integer negocioId,
            String nombre
    );

    // ==================================================
    // 💳 CLIENTES CON DEUDA
    // ==================================================
    //
    // Utilizado posteriormente por ABONOS.
    //
    List<Cliente>
    findByNegocioIdAndEstadoTrueAndDeudaGreaterThanOrderByNombreAsc(
            Integer negocioId,
            Double deuda
    );

    // ==================================================
    // 🔢 CONTAR CLIENTES ACTIVOS
    // ==================================================
    Long countByNegocioIdAndEstadoTrue(
            Integer negocioId
    );

    // ==================================================
    // 🔍 BUSCAR POR ID + NEGOCIO
    // ==================================================
    //
    // 🔐 MUY IMPORTANTE
    //
    // No basta con buscar solamente por ID.
    //
    // Si negocio 2 intenta:
    //
    // /api/clientes/15
    //
    // y el cliente 15 pertenece al negocio 1,
    // este método NO lo devuelve.
    //
    Optional<Cliente> findByIdAndNegocioId(
            Integer id,
            Integer negocioId
    );
}

