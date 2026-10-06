// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.service;


// ======================================================
// 📚 IMPORTS
// ======================================================

import com.ecomarket.dto.FacturaDTO;
import com.ecomarket.dto.VentaDTO;

import com.ecomarket.model.Cliente;
import com.ecomarket.model.DetalleVenta;
import com.ecomarket.model.Devolucion;
import com.ecomarket.model.Factura;
import com.ecomarket.model.Inventario;
import com.ecomarket.model.Producto;
import com.ecomarket.model.Venta;

import com.ecomarket.repository.ClienteRepository;
import com.ecomarket.repository.DetalleVentaRepository;
import com.ecomarket.repository.DevolucionRepository;
import com.ecomarket.repository.FacturaRepository;
import com.ecomarket.repository.InventarioRepository;
import com.ecomarket.repository.ProductoRepository;
import com.ecomarket.repository.VentaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * ======================================================
 * 🧾 SERVICIO DE VENTAS
 * ======================================================
 *
 * Contiene la lógica principal del módulo de ventas
 * de EcoMarket PRO.
 *
 * ✔ Crear ventas
 * ✔ Validar productos
 * ✔ Validar inventario
 * ✔ Descontar stock
 * ✔ Validar clientes
 * ✔ Ventas a crédito
 * ✔ Actualizar deuda
 * ✔ Crear factura
 * ✔ Consultar factura
 * ✔ Consultar devoluciones
 * ✔ Calcular totales
 * ✔ Aislamiento por negocio
 *
 * ======================================================
 *
 * 🔐 SEGURIDAD MULTI-NEGOCIO
 * ======================================================
 *
 * Todas las operaciones utilizan el negocioId
 * correspondiente al usuario autenticado.
 *
 * No se confía en el negocioId enviado desde
 * el frontend.
 *
 * Esto evita que un negocio pueda consultar o
 * modificar información de otro negocio.
 *
 * ======================================================
 */
@Service
@Transactional
public class VentaService {


    // ==================================================
    // 📦 REPOSITORIOS
    // ==================================================

    private final VentaRepository ventaRepository;

    private final DetalleVentaRepository detalleVentaRepository;

    private final FacturaRepository facturaRepository;

    private final ProductoRepository productoRepository;

    private final InventarioRepository inventarioRepository;

    private final ClienteRepository clienteRepository;

    private final DevolucionRepository devolucionRepository;


    // ==================================================
    // 🔧 CONSTRUCTOR
    // ==================================================

    public VentaService(
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            FacturaRepository facturaRepository,
            ProductoRepository productoRepository,
            InventarioRepository inventarioRepository,
            ClienteRepository clienteRepository,
            DevolucionRepository devolucionRepository
    ) {

        this.ventaRepository = ventaRepository;

        this.detalleVentaRepository =
                detalleVentaRepository;

        this.facturaRepository =
                facturaRepository;

        this.productoRepository =
                productoRepository;

        this.inventarioRepository =
                inventarioRepository;

        this.clienteRepository =
                clienteRepository;

        this.devolucionRepository =
                devolucionRepository;
    }


    // ==================================================
    // 🧾 CREAR VENTA
    // ==================================================

    /**
     * Crea una venta completa.
     *
     * @param dto datos enviados desde el POS
     * @param negocioId negocio del usuario autenticado
     * @return venta creada
     */
    public Venta crearVenta(
            VentaDTO dto,
            Integer negocioId
    ) {


        // ==================================================
        // 🔐 VALIDAR NEGOCIO
        // ==================================================

        validarNegocio(
                negocioId
        );


        // ==================================================
        // 🔎 VALIDAR DTO
        // ==================================================

        if (dto == null) {

            throw new IllegalArgumentException(
                    "Los datos de la venta son obligatorios."
            );
        }


        // ==================================================
        // 🛒 VALIDAR DETALLES
        // ==================================================

        if (dto.getDetalles() == null
                || dto.getDetalles().isEmpty()) {

            throw new IllegalArgumentException(
                    "La venta debe contener al menos un producto."
            );
        }


        // ==================================================
        // 💰 CALCULAR SUBTOTAL
        // ==================================================

        double subtotal = 0.0;


        for (VentaDTO.DetalleDTO detalleDTO
                : dto.getDetalles()) {


            // ==============================================
            // 🔎 VALIDAR DETALLE
            // ==============================================

            if (detalleDTO == null) {

                throw new IllegalArgumentException(
                        "Existe un detalle de venta inválido."
                );
            }


            // ==============================================
            // 📦 VALIDAR PRODUCTO
            // ==============================================

            if (detalleDTO.getProductoId() == null) {

                throw new IllegalArgumentException(
                        "Todos los productos de la venta son obligatorios."
                );
            }


            // ==============================================
            // 🔢 VALIDAR CANTIDAD
            // ==============================================

            if (detalleDTO.getCantidad() == null
                    || detalleDTO.getCantidad() <= 0) {

                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor que cero."
                );
            }


            // ==============================================
            // 💰 VALIDAR PRECIO
            // ==============================================

            if (detalleDTO.getPrecio() == null
                    || detalleDTO.getPrecio() < 0) {

                throw new IllegalArgumentException(
                        "El precio no puede ser negativo."
                );
            }


            // ==============================================
            // 💵 SUBTOTAL DEL PRODUCTO
            // ==============================================

            double subtotalDetalle =
                    detalleDTO.getCantidad()
                            * detalleDTO.getPrecio();


            subtotal +=
                    subtotalDetalle;
        }


        // ==================================================
        // 🧮 CALCULAR IVA
        // ==================================================

        double porcentajeIva = 19.0;


        if (dto.getPorcentajeIva() != null
                && dto.getPorcentajeIva() >= 0) {

            porcentajeIva =
                    dto.getPorcentajeIva();
        }


        double iva = 0.0;


        if (Boolean.TRUE.equals(
                dto.getAplicarIva()
        )) {

            iva =
                    subtotal
                            * (porcentajeIva / 100.0);
        }


        // ==================================================
        // 💰 CALCULAR TOTAL
        // ==================================================

        double total =
                subtotal + iva;


        // ==================================================
        // 🧾 CREAR VENTA
        // ==================================================

        Venta venta =
                new Venta();


        // ==================================================
        // 🔐 ASIGNAR NEGOCIO
        // ==================================================

        /*
         * IMPORTANTE:
         *
         * No utilizamos dto.getNegocioId().
         *
         * El negocio pertenece al usuario autenticado.
         */

        venta.setNegocioId(
                negocioId
        );


        // ==================================================
        // 👤 USUARIO
        // ==================================================

        if (dto.getUsuarioId() == null) {

            throw new IllegalArgumentException(
                    "El usuario que realiza la venta es obligatorio."
            );
        }


        venta.setIdUsuario(
                dto.getUsuarioId()
        );


        // ==================================================
        // 👥 CLIENTE
        // ==================================================

        if (dto.getClienteId() != null) {

            Cliente cliente =
                    clienteRepository
                            .findByIdAndNegocioId(
                                    dto.getClienteId(),
                                    negocioId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "El cliente no pertenece al negocio actual."
                                    )
                            );


            venta.setIdCliente(
                    cliente.getId()
            );
        }


        // ==================================================
        // 💰 TOTAL
        // ==================================================

        venta.setTotal(
                total
        );


        // ==================================================
        // 📅 FECHA
        // ==================================================

        venta.setFecha(
                LocalDateTime.now()
        );


        // ==================================================
        // 💳 MÉTODO DE PAGO
        // ==================================================

        String metodoPago =
                dto.getMetodoPago();


        if (metodoPago == null
                || metodoPago.isBlank()) {

            throw new IllegalArgumentException(
                    "El método de pago es obligatorio."
            );
        }


        metodoPago =
                metodoPago
                        .trim()
                        .toUpperCase(Locale.ROOT);


        venta.setMetodoPago(
                metodoPago
        );


        // ==================================================
        // 📦 ESTADO
        // ==================================================

        venta.setEstado(
                "COMPLETADA"
        );


        // ==================================================
        // 💵 EFECTIVO RECIBIDO
        // ==================================================

        double efectivoRecibido = 0.0;


        if (dto.getEfectivoRecibido() != null) {

            efectivoRecibido =
                    dto.getEfectivoRecibido();
        }


        // ==================================================
        // 💳 PAGO EN EFECTIVO
        // ==================================================

        if ("EFECTIVO".equals(metodoPago)) {

            if (efectivoRecibido < total) {

                throw new IllegalArgumentException(
                        "El efectivo recibido no puede ser menor al total de la venta."
                );
            }


            double cambio =
                    efectivoRecibido - total;


            venta.setEfectivoRecibido(
                    efectivoRecibido
            );


            venta.setCambio(
                    cambio
            );

        } else {

            // ==============================================
            // 💳 OTROS MÉTODOS
            // ==============================================

            venta.setEfectivoRecibido(
                    0.0
            );

            venta.setCambio(
                    0.0
            );
        }


        // ==================================================
        // 📲 WHATSAPP
        // ==================================================

        boolean enviarWhatsapp =
                Boolean.TRUE.equals(
                        dto.getEnviarWhatsapp()
                );


        venta.setEnviarWhatsapp(
                enviarWhatsapp
        );


        if (enviarWhatsapp) {

            String telefono =
                    dto.getTelefonoEnvio();


            if (telefono == null
                    || telefono.isBlank()) {

                throw new IllegalArgumentException(
                        "Debe indicar un teléfono para enviar la factura por WhatsApp."
                );
            }


            venta.setTelefonoEnvio(
                    telefono.trim()
            );

        } else {

            venta.setTelefonoEnvio(
                    null
            );
        }


        // ==================================================
        // 💳 VENTA A CRÉDITO / DEUDA
        // ==================================================

        if ("DEUDA".equals(metodoPago)) {


            // ==============================================
            // 👥 CLIENTE OBLIGATORIO
            // ==============================================

            if (dto.getClienteId() == null) {

                throw new IllegalArgumentException(
                        "Para registrar una venta a crédito debe seleccionar un cliente."
                );
            }


            Cliente cliente =
                    clienteRepository
                            .findByIdAndNegocioId(
                                    dto.getClienteId(),
                                    negocioId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "El cliente no pertenece al negocio actual."
                                    )
                            );


            // ==============================================
            // 💰 DEUDA ACTUAL
            // ==============================================

            double deudaActual = 0.0;


            if (cliente.getDeuda() != null) {

                deudaActual =
                        cliente.getDeuda();
            }


            // ==============================================
            // ➕ SUMAR NUEVA DEUDA
            // ==============================================

            cliente.setDeuda(
                    deudaActual + total
            );


            clienteRepository.save(
                    cliente
            );
        }


        // ==================================================
        // 💾 GUARDAR VENTA
        // ==================================================

        Venta ventaGuardada =
                ventaRepository.save(
                        venta
                );


        // ==================================================
        // 🧾 CREAR FACTURA
        // ==================================================
        //
        // IMPORTANTE:
        //
        // Antes solamente guardábamos:
        //
        // ✔ ventaId
        // ✔ fecha
        //
        // Eso provocaba que en la pantalla:
        //
        // Facturas
        //
        // aparecieran vacíos:
        //
        // ❌ Número de factura
        // ❌ Total
        //
        // Ahora copiamos todos los datos necesarios
        // desde la venta recién guardada.
        //
        // ==================================================

        Factura factura =
                new Factura();


        // ==================================================
        // 🔗 RELACIÓN CON VENTA
        // ==================================================

        factura.setVentaId(
                ventaGuardada.getId()
        );


        // ==================================================
        // 🧾 NÚMERO DE FACTURA
        // ==================================================

        factura.setNumeroFactura(
                ventaGuardada.getNumeroFactura()
        );


        // ==================================================
        // 💰 SUBTOTAL
        // ==================================================

        factura.setSubtotal(
                subtotal
        );


        // ==================================================
        // 🧮 IVA
        // ==================================================

        factura.setIva(
                iva
        );


        // ==================================================
        // 🧾 APLICA IVA
        // ==================================================

        factura.setAplicarIva(
                Boolean.TRUE.equals(
                        dto.getAplicarIva()
                )
        );


        // ==================================================
        // 💵 TOTAL
        // ==================================================

        factura.setTotal(
                total
        );


        // ==================================================
        // 📅 FECHA
        // ==================================================

        factura.setFecha(
                ventaGuardada.getFecha()
        );


        // ==================================================
        // 📦 ESTADO
        // ==================================================

        factura.setEstado(
                ventaGuardada.getEstado()
        );


        // ==================================================
        // 💾 GUARDAR FACTURA
        // ==================================================

        facturaRepository.save(
                factura
        );


        // ==================================================
        // 📦 PROCESAR PRODUCTOS
        // ==================================================

        for (VentaDTO.DetalleDTO detalleDTO
                : dto.getDetalles()) {


            // ==============================================
            // 🔐 BUSCAR PRODUCTO DEL NEGOCIO
            // ==============================================

            Producto producto =
                    productoRepository
                            .findByIdAndNegocioId(
                                    detalleDTO.getProductoId(),
                                    negocioId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "El producto seleccionado no pertenece al negocio actual."
                                    )
                            );


            // ==============================================
            // 🔎 PRODUCTO ACTIVO
            // ==============================================

            if (producto.getEstado() == null
                    || !producto.getEstado()) {

                throw new IllegalArgumentException(
                        "El producto "
                                + producto.getNombre()
                                + " está inactivo."
                );
            }


            // ==============================================
            // 📦 BUSCAR INVENTARIO
            // ==============================================

            Inventario inventario =
                    inventarioRepository
                            .findByProductoIdAndNegocioId(
                                    producto.getId(),
                                    negocioId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "El producto "
                                                    + producto.getNombre()
                                                    + " no tiene inventario registrado."
                                    )
                            );


            // ==============================================
            // 📊 STOCK ACTUAL
            // ==============================================

            Integer stockActual =
                    inventario.getStockActual();


            if (stockActual == null) {

                stockActual = 0;
            }


            // ==============================================
            // 🚫 VALIDAR STOCK
            // ==============================================

            if (stockActual
                    < detalleDTO.getCantidad()) {

                throw new IllegalArgumentException(
                        "Stock insuficiente para el producto: "
                                + producto.getNombre()
                                + ". Disponible: "
                                + stockActual
                );
            }


            // ==============================================
            // 📉 DESCONTAR STOCK
            // ==============================================

            inventario.setStockActual(
                    stockActual
                            - detalleDTO.getCantidad()
            );


            inventarioRepository.save(
                    inventario
            );


            // ==============================================
            // 🧾 CREAR DETALLE
            // ==============================================

            DetalleVenta detalle =
                    new DetalleVenta();


            // ==============================================
            // 🔗 VENTA
            // ==============================================

            detalle.setIdVenta(
                    ventaGuardada.getId()
            );


            // ==============================================
            // 📦 PRODUCTO
            // ==============================================

            detalle.setIdProducto(
                    producto.getId()
            );


            // ==============================================
            // 🏷 NOMBRE HISTÓRICO
            // ==============================================

            detalle.setNombreProducto(
                    producto.getNombre()
            );


            // ==============================================
            // 🔢 CANTIDAD
            // ==============================================

            detalle.setCantidad(
                    detalleDTO.getCantidad()
            );


            // ==============================================
            // 💰 PRECIO UNITARIO
            // ==============================================

            detalle.setPrecio(
                    detalleDTO.getPrecio()
            );


            // ==============================================
            // 💵 SUBTOTAL
            // ==============================================

            double subtotalDetalle =
                    detalleDTO.getCantidad()
                            * detalleDTO.getPrecio();


            detalle.setSubtotal(
                    subtotalDetalle
            );


            // ==============================================
            // 💾 GUARDAR DETALLE
            // ==============================================

            detalleVentaRepository.save(
                    detalle
            );
        }


        // ==================================================
        // ✅ RETORNAR VENTA
        // ==================================================

        return ventaGuardada;
    }


    // ==================================================
    // 🧾 OBTENER FACTURA
    // ==================================================

    /**
     * Obtiene los datos completos de una factura
     * a partir de la venta.
     *
     * 🔐 Solamente permite consultar ventas
     * pertenecientes al negocio actual.
     */
    @Transactional(readOnly = true)
    public FacturaDTO obtenerFactura(
            Integer ventaId,
            Integer negocioId
    ) {


        // ==================================================
        // 🔐 VALIDAR NEGOCIO
        // ==================================================

        validarNegocio(
                negocioId
        );


        // ==================================================
        // 🔐 BUSCAR VENTA SEGURA
        // ==================================================

        Venta venta =
                ventaRepository
                        .findByIdAndNegocioId(
                                ventaId,
                                negocioId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "La venta no pertenece al negocio actual."
                                )
                        );


        // ==================================================
        // 🧾 CREAR DTO
        // ==================================================

        FacturaDTO facturaDTO =
                new FacturaDTO();


        // ==================================================
        // 🧾 DATOS PRINCIPALES
        // ==================================================

        facturaDTO.setVentaId(
                venta.getId()
        );


        facturaDTO.setNumeroFactura(
                venta.getNumeroFactura()
        );


        facturaDTO.setFecha(
                venta.getFecha()
        );


        facturaDTO.setMetodoPago(
                venta.getMetodoPago()
        );


        facturaDTO.setEstado(
                venta.getEstado()
        );


        // ==================================================
        // 💰 TOTAL
        // ==================================================

        facturaDTO.setTotal(
                venta.getTotal()
        );


        // ==================================================
        // 💵 EFECTIVO
        // ==================================================

        facturaDTO.setEfectivoRecibido(
                venta.getEfectivoRecibido()
        );


        facturaDTO.setCambio(
                venta.getCambio()
        );


        // ==================================================
        // 👥 CLIENTE
        // ==================================================

        if (venta.getIdCliente() != null) {

            clienteRepository
                    .findByIdAndNegocioId(
                            venta.getIdCliente(),
                            negocioId
                    )
                    .ifPresent(cliente -> {

                        facturaDTO.setClienteNombre(
                                cliente.getNombre()
                        );

                        facturaDTO.setClienteTelefono(
                                cliente.getTelefono()
                        );
                    });
        }


        // ==================================================
        // 📦 DETALLES
        // ==================================================

        List<DetalleVenta> detalles =
                detalleVentaRepository
                        .findByIdVentaAndNegocioId(
                                ventaId,
                                negocioId
                        );


        List<FacturaDTO.DetalleFacturaDTO>
                detallesDTO =
                new ArrayList<>();


        double subtotal =
                0.0;


        for (DetalleVenta detalle
                : detalles) {


            FacturaDTO.DetalleFacturaDTO
                    detalleDTO =
                    new FacturaDTO.DetalleFacturaDTO();


            // ==============================================
            // 🏷 PRODUCTO
            // ==============================================

            detalleDTO.setProducto(
                    detalle.getNombreProducto()
            );


            // ==============================================
            // 🔢 CANTIDAD
            // ==============================================

            detalleDTO.setCantidad(
                    detalle.getCantidad()
            );


            // ==============================================
            // 💰 PRECIO
            // ==============================================

            detalleDTO.setPrecio(
                    detalle.getPrecio()
            );


            // ==============================================
            // 💵 SUBTOTAL
            // ==============================================

            double subtotalDetalle =
                    0.0;


            if (detalle.getSubtotal() != null) {

                subtotalDetalle =
                        detalle.getSubtotal();

            } else if (
                    detalle.getPrecio() != null
                            && detalle.getCantidad() != null
            ) {

                subtotalDetalle =
                        detalle.getPrecio()
                                * detalle.getCantidad();
            }


            detalleDTO.setSubtotal(
                    subtotalDetalle
            );


            subtotal +=
                    subtotalDetalle;


            detallesDTO.add(
                    detalleDTO
            );
        }


        facturaDTO.setDetalles(
                detallesDTO
        );


        // ==================================================
        // 🧮 IVA
        // ==================================================

        double iva =
                0.0;


        /*
         * La entidad Venta no almacena directamente
         * el porcentaje de IVA ni aplicarIva.
         *
         * Por eso calculamos la diferencia entre
         * el total y el subtotal.
         */

        if (venta.getTotal() != null
                && venta.getTotal() >= subtotal) {

            iva =
                    venta.getTotal()
                            - subtotal;
        }


        facturaDTO.setSubtotal(
                subtotal
        );


        facturaDTO.setIva(
                iva
        );


        // ==================================================
        // 🧾 APLICA IVA
        // ==================================================

        facturaDTO.setAplicarIva(
                iva > 0
        );


        // ==================================================
        // ↩️ DEVOLUCIONES
        // ==================================================

        List<Devolucion> devoluciones =
                devolucionRepository
                        .findByVentaIdAndNegocioId(
                                ventaId,
                                negocioId
                        );


        double totalDevuelto =
                0.0;


        if (devoluciones != null) {


            for (Devolucion devolucion
                    : devoluciones) {


                // ==========================================
                // 🚫 DEVOLUCIÓN ANULADA
                // ==========================================

                if (devolucion.getEstado() != null
                        && "ANULADA".equalsIgnoreCase(
                        devolucion.getEstado()
                )) {

                    continue;
                }


                // ==========================================
                // 💰 SUMAR DEVOLUCIÓN
                // ==========================================

                if (devolucion.getMonto() != null) {

                    totalDevuelto +=
                            devolucion.getMonto();
                }
            }
        }


        // ==================================================
        // 💰 TOTAL NETO
        // ==================================================

        double totalOriginal =
                venta.getTotal() != null
                        ? venta.getTotal()
                        : 0.0;


        double totalNeto =
                totalOriginal
                        - totalDevuelto;


        if (totalNeto < 0) {

            totalNeto = 0.0;
        }


        facturaDTO.setTotalDevuelto(
                totalDevuelto
        );


        facturaDTO.setTotalNeto(
                totalNeto
        );


        // ==================================================
        // 💰 TOTAL OFICIAL
        // ==================================================

        facturaDTO.setTotal(
                totalOriginal
        );


        // ==================================================
        // ✅ RETORNAR FACTURA
        // ==================================================

        return facturaDTO;
    }


    // ==================================================
    // 📋 LISTAR TODAS LAS VENTAS
    // ==================================================

    /**
     * Lista únicamente las ventas pertenecientes
     * al negocio actual.
     */
    @Transactional(readOnly = true)
    public List<Venta> listarTodas(
            Integer negocioId
    ) {


        // ==================================================
        // 🔐 VALIDAR NEGOCIO
        // ==================================================

        validarNegocio(
                negocioId
        );


        // ==================================================
        // 📋 CONSULTAR
        // ==================================================

        return ventaRepository
                .findByNegocioIdOrderByFechaDesc(
                        negocioId
                );
    }


    // ==================================================
    // 🔐 VALIDAR NEGOCIO
    // ==================================================

    /**
     * Verifica que exista un negocio asociado
     * al usuario autenticado.
     */
    private void validarNegocio(
            Integer negocioId
    ) {


        if (negocioId == null) {

            throw new IllegalStateException(
                    "No se pudo identificar el negocio del usuario autenticado."
            );
        }
    }
}