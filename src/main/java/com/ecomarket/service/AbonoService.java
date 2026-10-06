package com.ecomarket.service;

import com.ecomarket.model.Abono;
import com.ecomarket.model.Cliente;
import com.ecomarket.model.Venta;
import com.ecomarket.repository.AbonoRepository;
import com.ecomarket.repository.ClienteRepository;
import com.ecomarket.repository.VentaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ============================================================
 * 💰 ABONO SERVICE | ECOMARKET PRO
 * ============================================================
 *
 * Lógica de negocio de los abonos.
 *
 * ============================================================
 * 🔐 SEGURIDAD MULTI-NEGOCIO
 * ============================================================
 *
 * Todas las operaciones reciben negocioId.
 *
 * El negocio se obtiene desde:
 *
 * Usuario autenticado
 *        ↓
 * CustomUserDetails
 *        ↓
 * getNegocioId()
 *
 * Nunca se utiliza un negocioId enviado desde HTML/JavaScript.
 *
 * ============================================================
 */
@Service
public class AbonoService {


    private final AbonoRepository abonoRepository;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;


    public AbonoService(
            AbonoRepository abonoRepository,
            ClienteRepository clienteRepository,
            VentaRepository ventaRepository
    ) {
        this.abonoRepository = abonoRepository;
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
    }


    // ========================================================
    // 📋 LISTAR ABONOS
    // ========================================================

    public List<Abono> listarTodos(Integer negocioId) {

        validarNegocio(negocioId);

        return abonoRepository
                .findAllByNegocioIdOrderByFechaDesc(negocioId);
    }


    // ========================================================
    // 🔍 BUSCAR ABONO
    // ========================================================

    public Abono buscarPorId(
            Integer id,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (id == null) {
            throw new RuntimeException(
                    "El ID del abono es obligatorio."
            );
        }

        return abonoRepository
                .findByIdAndNegocioId(id, negocioId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "El abono no existe o no pertenece a este negocio."
                        )
                );
    }


    // ========================================================
    // 👥 CLIENTES CON DEUDA
    // ========================================================

    public List<Cliente> listarClientesConDeuda(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        return clienteRepository
                .findByNegocioIdAndEstadoTrueAndDeudaGreaterThanOrderByNombreAsc(
                        negocioId,
                        0.0
                );
    }


    // ========================================================
    // 🧾 FACTURAS DE DEUDA DE UN CLIENTE
    // ========================================================

    public List<Venta> listarFacturasDeuda(
            Integer idCliente,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (idCliente == null) {
            throw new RuntimeException(
                    "Debe seleccionar un cliente."
            );
        }

        // ----------------------------------------------------
        // Verificar que el cliente pertenezca al negocio
        // ----------------------------------------------------

        clienteRepository
                .findByIdAndNegocioId(idCliente, negocioId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "El cliente no pertenece a este negocio."
                        )
                );

        return ventaRepository
                .findFacturasDeudaPorCliente(
                        idCliente,
                        negocioId
                );
    }


    // ========================================================
    // 💰 CREAR ABONO
    // ========================================================

    @Transactional
    public Abono crear(
            Abono abono,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (abono == null) {
            throw new RuntimeException(
                    "Los datos del abono son obligatorios."
            );
        }

        if (abono.getIdCliente() == null) {
            throw new RuntimeException(
                    "Debe seleccionar un cliente."
            );
        }

        if (abono.getIdVenta() == null) {
            throw new RuntimeException(
                    "Debe seleccionar una factura."
            );
        }

        if (abono.getMonto() == null ||
                abono.getMonto() <= 0) {

            throw new RuntimeException(
                    "El monto del abono debe ser mayor a cero."
            );
        }


        // ====================================================
        // 👤 BUSCAR CLIENTE DEL NEGOCIO
        // ====================================================

        Cliente cliente = clienteRepository
                .findByIdAndNegocioId(
                        abono.getIdCliente(),
                        negocioId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "El cliente no existe o no pertenece a este negocio."
                        )
                );


        // ====================================================
        // 🧾 BUSCAR VENTA DEL NEGOCIO
        // ====================================================

        Venta venta = ventaRepository
                .findByIdAndNegocioId(
                        abono.getIdVenta(),
                        negocioId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "La factura no existe o no pertenece a este negocio."
                        )
                );


        // ====================================================
        // 🔐 VALIDAR CLIENTE DE LA FACTURA
        // ====================================================

        if (!abono.getIdCliente()
                .equals(venta.getIdCliente())) {

            throw new RuntimeException(
                    "La factura seleccionada no pertenece al cliente."
            );
        }


        // ====================================================
        // 💳 VALIDAR QUE SEA UNA VENTA A DEUDA
        // ====================================================

        if (venta.getMetodoPago() == null ||
                !"DEUDA".equalsIgnoreCase(
                        venta.getMetodoPago()
                )) {

            throw new RuntimeException(
                    "La factura seleccionada no corresponde a una venta a crédito."
            );
        }


        // ====================================================
        // 💰 DEUDA ACTUAL
        // ====================================================

        Double deudaActual = cliente.getDeuda();

        if (deudaActual == null) {
            deudaActual = 0.0;
        }


        if (deudaActual <= 0) {

            throw new RuntimeException(
                    "El cliente no tiene deuda pendiente."
            );
        }


        // ====================================================
        // 💰 VALIDAR MONTO
        // ====================================================

        if (abono.getMonto() > deudaActual) {

            throw new RuntimeException(
                    "El abono no puede ser mayor a la deuda actual del cliente."
            );
        }


        // ====================================================
        // ➖ DESCONTAR ABONO
        // ====================================================

        double nuevaDeuda =
                deudaActual - abono.getMonto();


        if (nuevaDeuda < 0) {
            nuevaDeuda = 0;
        }


        cliente.setDeuda(nuevaDeuda);

        clienteRepository.save(cliente);


        // ====================================================
        // 💾 GUARDAR ABONO
        // ====================================================

        return abonoRepository.save(abono);
    }


    // ========================================================
    // ✏️ ACTUALIZAR ABONO
    // ========================================================

    @Transactional
    public Abono actualizar(
            Integer id,
            Abono nuevoAbono,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (nuevoAbono == null) {
            throw new RuntimeException(
                    "Los datos del abono son obligatorios."
            );
        }


        // ====================================================
        // ABONO ORIGINAL
        // ====================================================

        Abono anterior =
                buscarPorId(id, negocioId);


        if (nuevoAbono.getIdCliente() == null) {
            throw new RuntimeException(
                    "Debe seleccionar un cliente."
            );
        }

        if (nuevoAbono.getIdVenta() == null) {
            throw new RuntimeException(
                    "Debe seleccionar una factura."
            );
        }

        if (nuevoAbono.getMonto() == null ||
                nuevoAbono.getMonto() <= 0) {

            throw new RuntimeException(
                    "El monto debe ser mayor a cero."
            );
        }


        // ====================================================
        // CLIENTE ANTERIOR
        // ====================================================

        Cliente clienteAnterior =
                clienteRepository
                        .findByIdAndNegocioId(
                                anterior.getIdCliente(),
                                negocioId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "El cliente anterior no existe."
                                )
                        );


        // ====================================================
        // DEVOLVER EL ABONO ANTERIOR
        // A LA DEUDA
        // ====================================================

        double deudaAnterior =
                clienteAnterior.getDeuda() == null
                        ? 0.0
                        : clienteAnterior.getDeuda();

        deudaAnterior += anterior.getMonto();

        clienteAnterior.setDeuda(deudaAnterior);

        clienteRepository.save(clienteAnterior);


        // ====================================================
        // NUEVO CLIENTE
        // ====================================================

        Cliente nuevoCliente =
                clienteRepository
                        .findByIdAndNegocioId(
                                nuevoAbono.getIdCliente(),
                                negocioId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "El nuevo cliente no existe o no pertenece a este negocio."
                                )
                        );


        // ====================================================
        // NUEVA FACTURA
        // ====================================================

        Venta nuevaVenta =
                ventaRepository
                        .findByIdAndNegocioId(
                                nuevoAbono.getIdVenta(),
                                negocioId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "La nueva factura no existe o no pertenece a este negocio."
                                )
                        );


        // ====================================================
        // VALIDAR CLIENTE DE LA FACTURA
        // ====================================================

        if (!nuevoAbono.getIdCliente()
                .equals(nuevaVenta.getIdCliente())) {

            throw new RuntimeException(
                    "La factura no pertenece al cliente seleccionado."
            );
        }


        // ====================================================
        // VALIDAR QUE SEA DEUDA
        // ====================================================

        if (nuevaVenta.getMetodoPago() == null ||
                !"DEUDA".equalsIgnoreCase(
                        nuevaVenta.getMetodoPago()
                )) {

            throw new RuntimeException(
                    "La factura seleccionada no corresponde a una venta a crédito."
            );
        }


        // ====================================================
        // NUEVA DEUDA
        // ====================================================

        double nuevaDeudaCliente =
                nuevoCliente.getDeuda() == null
                        ? 0.0
                        : nuevoCliente.getDeuda();


        if (nuevoAbono.getMonto() >
                nuevaDeudaCliente) {

            throw new RuntimeException(
                    "El nuevo monto supera la deuda del cliente."
            );
        }


        // ====================================================
        // DESCONTAR NUEVO ABONO
        // ====================================================

        nuevaDeudaCliente -=
                nuevoAbono.getMonto();


        if (nuevaDeudaCliente < 0) {
            nuevaDeudaCliente = 0;
        }


        nuevoCliente.setDeuda(
                nuevaDeudaCliente
        );

        clienteRepository.save(nuevoCliente);


        // ====================================================
        // ACTUALIZAR ABONO
        // ====================================================

        anterior.setIdCliente(
                nuevoAbono.getIdCliente()
        );

        anterior.setIdVenta(
                nuevoAbono.getIdVenta()
        );

        anterior.setMonto(
                nuevoAbono.getMonto()
        );

        anterior.setDescripcion(
                nuevoAbono.getDescripcion()
        );

        return abonoRepository.save(anterior);
    }


    // ========================================================
    // 🗑️ ELIMINAR ABONO
    // ========================================================

    @Transactional
    public void eliminar(
            Integer id,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        Abono abono =
                buscarPorId(id, negocioId);


        // ====================================================
        // DEVOLVER EL VALOR A LA DEUDA
        // ====================================================

        if (abono.getIdCliente() != null) {

            Cliente cliente =
                    clienteRepository
                            .findByIdAndNegocioId(
                                    abono.getIdCliente(),
                                    negocioId
                            )
                            .orElse(null);

            if (cliente != null) {

                double deuda =
                        cliente.getDeuda() == null
                                ? 0.0
                                : cliente.getDeuda();

                deuda += abono.getMonto();

                cliente.setDeuda(deuda);

                clienteRepository.save(cliente);
            }
        }


        // ====================================================
        // ELIMINAR ABONO
        // ====================================================

        abonoRepository.delete(abono);
    }


    // ========================================================
    // 🔐 VALIDAR NEGOCIO
    // ========================================================

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