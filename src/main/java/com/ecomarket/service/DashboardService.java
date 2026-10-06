// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.service;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Inventario;
import com.ecomarket.model.Venta;

import java.math.BigDecimal;
import java.util.List;

/**
 * ======================================================
 * 📊 SERVICE DASHBOARD
 * ======================================================
 *
 * Define las operaciones utilizadas por el Dashboard.
 *
 * 🔐 IMPORTANTE:
 *
 * Todos los métodos que consultan información del
 * negocio reciben negocioId.
 *
 * Esto permite que el Dashboard solamente muestre
 * información del negocio del usuario autenticado.
 *
 * ======================================================
 */
public interface DashboardService {

    // ==================================================
    // 💰 VENTAS DEL DÍA
    // ==================================================
    BigDecimal obtenerVentasHoy(
            Integer negocioId
    );

    // ==================================================
    // 📦 TOTAL PRODUCTOS
    // ==================================================
    Long obtenerTotalProductos(
            Integer negocioId
    );

    // ==================================================
    // 👥 TOTAL CLIENTES
    // ==================================================
    Long obtenerTotalClientes(
            Integer negocioId
    );

    // ==================================================
    // 📈 GANANCIAS / VENTAS DEL MES
    // ==================================================
    BigDecimal obtenerGananciasMes(
            Integer negocioId
    );

    // ==================================================
    // 💵 SALDO CAJA
    // ==================================================
    BigDecimal obtenerSaldoCaja(
            Integer negocioId
    );

    // ==================================================
    // 🧾 VENTAS RECIENTES
    // ==================================================
    List<Venta> obtenerVentasRecientes(
            Integer negocioId
    );

    // ==================================================
    // ⚠️ INVENTARIO BAJO
    // ==================================================
    List<Inventario> obtenerStockBajo(
            Integer negocioId
    );

    // ==================================================
    // 📊 VENTAS SEMANA
    // ==================================================
    List<Double> obtenerVentasSemana(
            Integer negocioId
    );
}