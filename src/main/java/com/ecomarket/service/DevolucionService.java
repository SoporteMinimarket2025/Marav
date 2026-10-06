package com.ecomarket.service;

import com.ecomarket.model.Devolucion;

import java.util.List;

/**
 * ============================================================
 * ↩️ DEVOLUCION SERVICE | ECOMARKET PRO
 * ============================================================
 *
 * Servicio encargado de gestionar las devoluciones.
 *
 * ============================================================
 * 🔐 SEGURIDAD MULTI-NEGOCIO
 * ============================================================
 *
 * El negocio se obtiene desde el usuario autenticado.
 *
 * Todas las operaciones reciben negocioId.
 *
 * Devolucion no tiene negocioId directamente.
 *
 * La relación es:
 *
 * Devolucion
 *      ↓
 * Venta
 *      ↓
 * negocioId
 *
 * ============================================================
 */
public interface DevolucionService {


    // ========================================================
    // 📋 LISTAR DEVOLUCIONES
    // ========================================================

    List<Devolucion> listar(
            Integer negocioId
    );


    // ========================================================
    // 💾 GUARDAR DEVOLUCIÓN
    // ========================================================

    Devolucion guardar(
            Devolucion devolucion,
            Integer negocioId
    );


    // ========================================================
    // ✏️ ACTUALIZAR DEVOLUCIÓN
    // ========================================================

    Devolucion actualizar(
            Integer id,
            Devolucion devolucion,
            Integer negocioId
    );


    // ========================================================
    // 🔍 BUSCAR DEVOLUCIÓN
    // ========================================================

    Devolucion buscarPorId(
            Integer id,
            Integer negocioId
    );


    // ========================================================
    // 🗑️ ELIMINAR DEVOLUCIÓN
    // ========================================================

    void eliminar(
            Integer id,
            Integer negocioId
    );
}