package com.ecomarket.service;

import com.ecomarket.model.Proveedor;

import java.util.List;

/**
 * ============================================================
 * SERVICIO DE PROVEEDORES
 * ============================================================
 *
 * Contiene las operaciones relacionadas con proveedores.
 *
 * IMPORTANTE:
 * Todas las operaciones reciben negocioId para garantizar
 * el aislamiento de datos entre diferentes negocios.
 *
 * Ejemplo:
 *
 * Negocio 1 -> solo puede consultar sus proveedores.
 * Negocio 2 -> solo puede consultar sus proveedores.
 *
 * El negocio NO se recibe desde el formulario.
 * Se obtiene desde el usuario autenticado.
 * ============================================================
 */
public interface ProveedorService {

    /**
     * Lista todos los proveedores pertenecientes
     * al negocio autenticado.
     */
    List<Proveedor> listar(
            Integer negocioId
    );

    /**
     * Guarda un nuevo proveedor asociado al negocio.
     */
    Proveedor guardar(
            Proveedor proveedor,
            Integer negocioId
    );

    /**
     * Busca un proveedor por ID, pero únicamente
     * si pertenece al negocio indicado.
     */
    Proveedor buscarPorId(
            Integer id,
            Integer negocioId
    );

    /**
     * Elimina un proveedor únicamente si
     * pertenece al negocio indicado.
     */
    void eliminar(
            Integer id,
            Integer negocioId
    );

    /**
     * Busca proveedores por nombre dentro
     * del negocio autenticado.
     */
    List<Proveedor> buscar(
            String nombre,
            Integer negocioId
    );
}

