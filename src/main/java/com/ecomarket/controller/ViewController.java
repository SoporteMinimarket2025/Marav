
// ======================================================
// 📁 PACKAGE
// ======================================================
        package com.ecomarket.controller;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.dto.FacturaDTO;
import com.ecomarket.security.CustomUserDetails;
import com.ecomarket.service.VentaService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * ======================================================
 * 🎨 CONTROLADOR GENERAL DE VISTAS HTML
 * ======================================================
 *
 * ✔ Dashboard moderno
 * ✔ Productos
 * ✔ Inventario
 * ✔ Proveedores
 * ✔ Clientes
 * ✔ Ventas POS
 * ✔ Facturación
 * ✔ Reportes
 * ✔ Simulador Solar
 * ✔ Usuarios / Empleados
 *
 * ======================================================
 */
@Controller
public class ViewController {

    // ======================================================
    // 💳 SERVICE VENTAS
    // ======================================================
    @Autowired
    private VentaService ventaService;

    // ======================================================
    // 🏠 INICIO
    // ======================================================
    @GetMapping("/")
    public String inicio() {

        // 🔥 Redireccionar al login
        return "redirect:/login";
    }

    // ======================================================
    // 🔐 LOGIN
    // ======================================================
    @GetMapping("/login")
    public String login() {

        // 🎨 Abrir login.html
        return "login";
    }


    // ======================================================
    // 📦 PRODUCTOS
    // ======================================================
    @GetMapping("/productos")
    public String productos(
            Authentication auth,
            Model model
    ) {

        // 🔥 Datos usuario
        cargarDatosUsuario(auth, model);

        // 📦 Activar menú Productos
        model.addAttribute(
                "menuActivo",
                "productos"
        );

        return "productos";
    }

    // ======================================================
    // 🏪 INVENTARIO
    // ======================================================
    @GetMapping("/inventario")
    public String inventario(
            Authentication auth,
            Model model
    ) {

        cargarDatosUsuario(auth, model);

        model.addAttribute(
                "menuActivo",
                "inventario"
        );

        return "inventario";
    }


    // ======================================================
    // 👥 CLIENTES
    // ======================================================
    @GetMapping("/clientes")
    public String clientes(
            Authentication auth,
            Model model
    ) {

        // 🔥 Datos usuario
        cargarDatosUsuario(auth, model);

        // 🎨 Abrir clientes.html
        return "clientes";
    }

    // ======================================================
    // 🛒 VENTAS POS
    // ======================================================
    @GetMapping("/ventas")
    public String ventas(
            Authentication auth,
            Model model
    ) {

        // 🔥 Datos usuario
        cargarDatosUsuario(auth, model);

        // 🎨 Abrir ventas.html
        return "ventas";
    }
    // ======================================================
// ↩️ DEVOLUCIONES
// ======================================================
    @GetMapping("/devoluciones")
    public String devoluciones(
            Authentication auth,
            Model model
    ) {

        // 🔥 Datos usuario
        cargarDatosUsuario(auth, model);

        // 🔥 Menú activo
        model.addAttribute(
                "menuActivo",
                "devoluciones"
        );

        // 🎨 Abrir devoluciones.html
        return "devoluciones";
    }


    // ======================================================
    // 📈 REPORTES
    // ======================================================
    @GetMapping("/reportes")
    public String reportes(
            Authentication auth,
            Model model
    ) {

        // 🔥 Datos usuario
        cargarDatosUsuario(auth, model);

        // 🎨 Abrir reportes.html
        return "reportes";
    }

    // ======================================================
    // ☀️ SIMULADOR SOLAR
    // ======================================================
    @GetMapping("/simulador")
    public String simulador(
            Authentication auth,
            Model model
    ) {

        // 🔥 Datos usuario
        cargarDatosUsuario(auth, model);

        // 🎨 Abrir simulador.html
        return "simulador";
    }
    // ======================================================
// 👨‍💼 USUARIOS / EMPLEADOS
// ======================================================
    @GetMapping("/usuarios")
    public String usuarios(
            Authentication auth,
            Model model
    ) {


        // 🔥 Datos usuario
        cargarDatosUsuario(auth, model);



        // 🎨 Menú activo
        model.addAttribute(
                "menuActivo",
                "usuarios"
        );



        // 📄 Vista
        return "usuarios";

    }


    // ======================================================
    // ⚙️ CONFIGURACIÓN
    // ======================================================
    @GetMapping("/configuracion")
    public String configuracion(
            Authentication auth,
            Model model
    ) {

        // 🔥 Datos usuario
        cargarDatosUsuario(auth, model);

        // 🎨 Abrir configuracion.html
        return "configuracion";
    }

    // ======================================================
    // 💵 CAJA
    // ======================================================
    @GetMapping("/caja")
    public String caja(
            Authentication auth,
            Model model
    ) {

        // 🔥 Datos usuario
        cargarDatosUsuario(auth, model);

        // 🎨 Abrir caja.html
        return "caja";
    }

    // ======================================================
// 🔥 MÉTODO PRIVADO
// CARGAR DATOS USUARIO LOGUEADO
// ======================================================
    private void cargarDatosUsuario(
            Authentication auth,
            Model model
    ) {

        // ==================================================
        // 🛡️ VALIDAR SESIÓN
        // ==================================================

        if (auth == null ||
                !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return;
        }

        // ==================================================
        // 🔐 USUARIO AUTENTICADO
        // ==================================================

        CustomUserDetails user =
                (CustomUserDetails) auth.getPrincipal();

        // ==================================================
        // 👤 NOMBRE USUARIO
        // ==================================================

        model.addAttribute(
                "usuario",
                user.getNombre()
        );

        // ==================================================
        // 🏪 NEGOCIO ID
        // ==================================================

        model.addAttribute(
                "negocioId",
                user.getNegocioId()
        );

        // ==================================================
        // 🔐 ROL USUARIO
        // ==================================================

        String rol = user.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", "");

        model.addAttribute(
                "rolUsuario",
                rol
        );

    }
}

