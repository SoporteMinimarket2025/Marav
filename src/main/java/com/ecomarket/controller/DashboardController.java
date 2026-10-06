// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.controller;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.DashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ======================================================
 * 📊 CONTROLLER DASHBOARD
 * ======================================================
 *
 * Controlador principal del Dashboard de EcoMarket PRO.
 *
 * 🔐 IMPORTANTE:
 *
 * El Dashboard trabaja con el negocio del usuario
 * autenticado.
 *
 * Flujo:
 *
 * LOGIN
 *   ↓
 * CustomUserDetails
 *   ↓
 * negocioId
 *   ↓
 * DashboardController
 *   ↓
 * DashboardService
 *   ↓
 * Repository filtrado por negocioId
 *
 * De esta manera cada negocio solamente visualiza
 * sus propios productos, clientes, ventas e inventario.
 *
 * ======================================================
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    // ==================================================
    // 🔗 SERVICE
    // ==================================================

    private final DashboardService dashboardService;


    // ==================================================
    // 📊 DASHBOARD
    // ==================================================

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication auth,
            Model model
    ) {

        // ==================================================
        // 🔐 VALIDAR USUARIO AUTENTICADO
        // ==================================================

        if (auth == null ||
                !(auth.getPrincipal()
                        instanceof CustomUserDetails user)) {

            // ------------------------------------------------
            // Si por alguna razón no existe un usuario
            // autenticado, regresamos al login.
            // ------------------------------------------------

            return "redirect:/login";
        }


        // ==================================================
        // 🏪 OBTENER NEGOCIO DEL USUARIO
        // ==================================================

        Integer negocioId =
                user.getNegocioId();


        // ==================================================
        // 🔐 VALIDAR NEGOCIO
        // ==================================================

        if (negocioId == null) {

            // ------------------------------------------------
            // Un usuario de EcoMarket PRO debe estar asociado
            // a un negocio.
            // ------------------------------------------------

            throw new IllegalStateException(
                    "El usuario no tiene un negocio asociado."
            );
        }


        // ==================================================
        // 👤 DATOS DEL USUARIO LOGUEADO
        // ==================================================

        model.addAttribute(
                "usuario",
                user.getNombre()
        );

        model.addAttribute(
                "rolUsuario",
                user.getRol()
        );

        // --------------------------------------------------
        // También enviamos el negocio al HTML.
        //
        // Esto puede ser utilizado posteriormente por
        // Thymeleaf o JavaScript si fuera necesario.
        // --------------------------------------------------

        model.addAttribute(
                "negocioId",
                negocioId
        );


        // ==================================================
        // 📌 MENÚ ACTIVO
        // ==================================================

        model.addAttribute(
                "menuActivo",
                "dashboard"
        );


        // ==================================================
        // 📊 MÉTRICAS DEL DASHBOARD
        // ==================================================
        //
        // 🔐 MUY IMPORTANTE:
        //
        // Todos los métodos reciben negocioId.
        //
        // Así evitamos consultar datos globales.
        //
        // ==================================================


        // --------------------------------------------------
        // 💰 VENTAS DE HOY
        // --------------------------------------------------

        model.addAttribute(
                "ventasHoy",
                dashboardService.obtenerVentasHoy(
                        negocioId
                )
        );


        // --------------------------------------------------
        // 📦 TOTAL PRODUCTOS
        // --------------------------------------------------

        model.addAttribute(
                "totalProductos",
                dashboardService.obtenerTotalProductos(
                        negocioId
                )
        );


        // --------------------------------------------------
        // 👥 TOTAL CLIENTES
        // --------------------------------------------------

        model.addAttribute(
                "totalClientes",
                dashboardService.obtenerTotalClientes(
                        negocioId
                )
        );


        // --------------------------------------------------
        // 📈 GANANCIAS DEL MES
        // --------------------------------------------------

        model.addAttribute(
                "gananciasMes",
                dashboardService.obtenerGananciasMes(
                        negocioId
                )
        );


        // --------------------------------------------------
        // 💵 SALDO CAJA
        // --------------------------------------------------
        //
        // Actualmente utiliza las ventas del día porque
        // todavía no terminamos el módulo Caja.
        //
        // Posteriormente se conectará al saldo real.
        //
        // --------------------------------------------------

        model.addAttribute(
                "saldoCaja",
                dashboardService.obtenerSaldoCaja(
                        negocioId
                )
        );


        // --------------------------------------------------
        // 🧾 VENTAS RECIENTES
        // --------------------------------------------------

        model.addAttribute(
                "ventasRecientes",
                dashboardService.obtenerVentasRecientes(
                        negocioId
                )
        );


        // --------------------------------------------------
        // ⚠️ INVENTARIO BAJO
        // --------------------------------------------------

        model.addAttribute(
                "inventarioBajo",
                dashboardService.obtenerStockBajo(
                        negocioId
                )
        );


        // ==================================================
        // 🖥️ VISTA
        // ==================================================

        return "dashboard";
    }
}