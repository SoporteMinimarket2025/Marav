// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.service.impl;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Inventario;
import com.ecomarket.model.Venta;

import com.ecomarket.repository.ClienteRepository;
import com.ecomarket.repository.InventarioRepository;
import com.ecomarket.repository.ProductoRepository;
import com.ecomarket.repository.VentaRepository;

import com.ecomarket.service.DashboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * ======================================================
 * 📊 SERVICE IMPLEMENTACIÓN DASHBOARD
 * ======================================================
 *
 * 🔐 Este Service trabaja por negocio.
 *
 * El negocioId debe venir del usuario autenticado.
 *
 * Nunca se debe utilizar:
 *
 * repository.count()
 * repository.findAll()
 *
 * para mostrar información de un negocio.
 *
 * En su lugar se utilizan consultas filtradas
 * mediante negocioId.
 *
 * ======================================================
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl
        implements DashboardService {

    // ==================================================
    // 🔗 REPOSITORIES
    // ==================================================

    private final VentaRepository ventaRepository;

    private final ProductoRepository productoRepository;

    private final ClienteRepository clienteRepository;

    private final InventarioRepository inventarioRepository;


    // ==================================================
    // 💰 VENTAS DE HOY
    // ==================================================
    @Override
    public BigDecimal obtenerVentasHoy(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        Double total =
                ventaRepository.totalVentasHoy(
                        negocioId
                );

        return BigDecimal.valueOf(
                total != null ? total : 0.0
        );
    }


    // ==================================================
    // 📦 TOTAL PRODUCTOS
    // ==================================================
    @Override
    public Long obtenerTotalProductos(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        return productoRepository
                .countByNegocioIdAndEstadoTrue(
                        negocioId
                );
    }


    // ==================================================
    // 👥 TOTAL CLIENTES
    // ==================================================
    @Override
    public Long obtenerTotalClientes(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        return clienteRepository
                .countByNegocioIdAndEstadoTrue(
                        negocioId
                );
    }


    // ==================================================
    // 📈 GANANCIAS / VENTAS DEL MES
    // ==================================================
    @Override
    public BigDecimal obtenerGananciasMes(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        Double total =
                ventaRepository.totalVentasMes(
                        negocioId
                );

        return BigDecimal.valueOf(
                total != null ? total : 0.0
        );
    }


    // ==================================================
    // 💵 SALDO DE CAJA
    // ==================================================
    //
    // ⚠️ TEMPORALMENTE utiliza las ventas del día,
    // igual que la versión anterior.
    //
    // Posteriormente, cuando terminemos el módulo
    // CAJA, este método debe consultar realmente
    // el saldo de caja del negocio.
    //
    // ==================================================
    @Override
    public BigDecimal obtenerSaldoCaja(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        Double total =
                ventaRepository.totalVentasHoy(
                        negocioId
                );

        return BigDecimal.valueOf(
                total != null ? total : 0.0
        );
    }


    // ==================================================
    // 🧾 VENTAS RECIENTES
    // ==================================================
    @Override
    public List<Venta> obtenerVentasRecientes(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        return ventaRepository
                .findTop10ByNegocioIdOrderByFechaDesc(
                        negocioId
                );
    }


    // ==================================================
    // ⚠️ INVENTARIO BAJO
    // ==================================================
    @Override
    public List<Inventario> obtenerStockBajo(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        return inventarioRepository
                .obtenerStockBajo(
                        negocioId
                );
    }


    // ==================================================
    // 📊 VENTAS DE LA SEMANA
    // ==================================================
    //
    // ⚠️ Actualmente estos valores son DEMO.
    //
    // No están conectados todavía a la base de datos.
    //
    // Los dejamos temporalmente para no romper
    // el gráfico existente.
    //
    // ==================================================
    @Override
    public List<Double> obtenerVentasSemana(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        return List.of(
                150.0,
                280.0,
                190.0,
                350.0,
                420.0,
                510.0,
                640.0
        );
    }


    // ==================================================
    // 🔐 VALIDAR NEGOCIO
    // ==================================================
    private void validarNegocio(
            Integer negocioId
    ) {

        if (negocioId == null) {

            throw new IllegalArgumentException(
                    "El usuario no tiene un negocio asociado."
            );
        }
    }
}