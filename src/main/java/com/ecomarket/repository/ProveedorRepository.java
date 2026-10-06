package com.ecomarket.repository;

import com.ecomarket.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * REPOSITORY DE PROVEEDORES
 * ============================================================
 *
 * Este repositorio permite consultar los proveedores
 * pertenecientes a un negocio específico.
 *
 * IMPORTANTE:
 *
 * EcoMarket PRO es multi-negocio.
 *
 * Por eso las consultas principales utilizan negocioId.
 *
 * De esta manera:
 *
 * Negocio 1 -> solo obtiene proveedores del negocio 1.
 * Negocio 2 -> solo obtiene proveedores del negocio 2.
 *
 * Esto evita mezclar información entre negocios.
 * ============================================================
 */
@Repository
public interface ProveedorRepository
        extends JpaRepository<Proveedor, Integer> {


    // =========================================================
    // LISTAR PROVEEDORES DE UN NEGOCIO
    // =========================================================

    /**
     * Obtiene todos los proveedores pertenecientes
     * al negocio indicado.
     */
    List<Proveedor> findByNegocioId(
            Integer negocioId
    );


    // =========================================================
    // BUSCAR PROVEEDOR POR ID Y NEGOCIO
    // =========================================================

    /**
     * Busca un proveedor por su ID y verifica que
     * pertenezca al negocio indicado.
     *
     * Esto es importante para evitar que un negocio
     * consulte o modifique proveedores de otro negocio.
     */
    Optional<Proveedor> findByIdAndNegocioId(
            Integer id,
            Integer negocioId
    );


    // =========================================================
    // BUSCAR POR NOMBRE DENTRO DEL NEGOCIO
    // =========================================================

    /**
     * Busca proveedores por nombre ignorando mayúsculas
     * y minúsculas, pero únicamente dentro del negocio.
     *
     * Ejemplo:
     *
     * Negocio 1 busca "distribuidora"
     *
     * -> Solo devuelve proveedores del negocio 1.
     */
    List<Proveedor> findByNegocioIdAndNombreContainingIgnoreCase(
            Integer negocioId,
            String nombre
    );
}

