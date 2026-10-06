package com.ecomarket.service.impl;

import com.ecomarket.model.Devolucion;
import com.ecomarket.model.Inventario;
import com.ecomarket.repository.DevolucionRepository;
import com.ecomarket.repository.InventarioRepository;
import com.ecomarket.service.DevolucionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ============================================================
 * ↩️ DEVOLUCION SERVICE IMPL | ECOMARKET PRO
 * ============================================================
 *
 * Implementación de la lógica de devoluciones.
 *
 * ============================================================
 * 🔐 SEGURIDAD MULTI-NEGOCIO
 * ============================================================
 *
 * La devolución no posee negocioId directamente.
 *
 * Su negocio se determina mediante:
 *
 *      Devolucion
 *           ↓
 *         Venta
 *           ↓
 *      negocioId
 *
 * Por lo tanto nunca utilizamos:
 *
 * repository.findAll()
 * repository.findById()
 *
 * para operaciones que puedan exponer datos.
 *
 * ============================================================
 */
@Service
public class DevolucionServiceImpl
        implements DevolucionService {


    private final DevolucionRepository repository;

    private final InventarioRepository inventarioRepository;


    public DevolucionServiceImpl(
            DevolucionRepository repository,
            InventarioRepository inventarioRepository
    ) {

        this.repository =
                repository;

        this.inventarioRepository =
                inventarioRepository;
    }


    // ========================================================
    // 📋 LISTAR
    // ========================================================

    @Override
    public List<Devolucion> listar(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        return repository
                .findAllByNegocioId(
                        negocioId
                );
    }


    // ========================================================
    // 💾 GUARDAR
    // ========================================================

    @Override
    @Transactional
    public Devolucion guardar(
            Devolucion devolucion,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        validarDevolucion(
                devolucion
        );


        /*
         * ====================================================
         * 🔐 VALIDAR QUE LA VENTA PERTENEZCA AL NEGOCIO
         * ====================================================
         *
         * El repository de devoluciones ya utiliza la venta
         * para comprobar el negocio.
         *
         * Aquí verificamos que exista una venta válida.
         *
         * La devolución recibida debe tener una Venta.
         */

        if (devolucion.getVenta() == null ||
                devolucion.getVenta().getId() == null) {

            throw new RuntimeException(
                    "Debe seleccionar una venta."
            );
        }


        /*
         * ====================================================
         * 🔐 VALIDAR PRODUCTO
         * ====================================================
         *
         * El producto debe pertenecer a una devolución
         * relacionada con una venta del negocio.
         *
         */

        if (devolucion.getProducto() == null ||
                devolucion.getProducto().getId() == null) {

            throw new RuntimeException(
                    "Debe seleccionar un producto."
            );
        }


        // ====================================================
        // ↩️ AUMENTAR INVENTARIO
        // ====================================================

        if (
                "ACTIVA".equalsIgnoreCase(
                        devolucion.getEstado()
                )
        ) {

            aumentarInventario(
                    devolucion
                            .getProducto()
                            .getId(),

                    devolucion.getCantidad(),

                    negocioId
            );
        }


        // ====================================================
        // 💾 GUARDAR
        // ====================================================

        return repository.save(
                devolucion
        );
    }


    // ========================================================
    // ✏️ ACTUALIZAR
    // ========================================================

    @Override
    @Transactional
    public Devolucion actualizar(
            Integer id,
            Devolucion nueva,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (nueva == null) {

            throw new RuntimeException(
                    "Los datos de la devolución son obligatorios."
            );
        }


        // ====================================================
        // 🔐 BUSCAR DEVOLUCIÓN DEL NEGOCIO
        // ====================================================

        Devolucion actual =
                buscarPorId(
                        id,
                        negocioId
                );


        if (actual == null) {

            return null;
        }


        validarDevolucion(
                nueva
        );


        // ====================================================
        // DATOS ANTERIORES
        // ====================================================

        String estadoAnterior =
                actual.getEstado();


        int cantidadAnterior =
                actual.getCantidad() == null
                        ? 0
                        : actual.getCantidad();


        String estadoNuevo =
                nueva.getEstado();


        int cantidadNueva =
                nueva.getCantidad() == null
                        ? 0
                        : nueva.getCantidad();


        Integer productoAnteriorId =
                actual.getProducto() == null
                        ? null
                        : actual.getProducto().getId();


        Integer productoNuevoId =
                nueva.getProducto() == null
                        ? null
                        : nueva.getProducto().getId();


        // ====================================================
        // 🔄 CAMBIO DE PRODUCTO
        // ====================================================

        if (
                productoAnteriorId != null &&
                        productoNuevoId != null &&
                        !productoAnteriorId.equals(
                                productoNuevoId
                        )
        ) {

            /*
             * Si se cambia el producto de una devolución
             * activa, primero retiramos del inventario el
             * efecto de la devolución anterior.
             */

            if (
                    "ACTIVA".equalsIgnoreCase(
                            estadoAnterior
                    )
            ) {

                disminuirInventario(
                        productoAnteriorId,
                        cantidadAnterior,
                        negocioId
                );
            }


            /*
             * Después aplicamos la nueva devolución
             * al nuevo producto.
             */

            if (
                    "ACTIVA".equalsIgnoreCase(
                            estadoNuevo
                    )
            ) {

                aumentarInventario(
                        productoNuevoId,
                        cantidadNueva,
                        negocioId
                );
            }

        } else {


            // =================================================
            // CASO 1
            // ACTIVA → ANULADA
            // =================================================

            if (
                    "ACTIVA".equalsIgnoreCase(
                            estadoAnterior
                    )
                            &&
                            "ANULADA".equalsIgnoreCase(
                                    estadoNuevo
                            )
            ) {

                disminuirInventario(
                        productoAnteriorId,
                        cantidadAnterior,
                        negocioId
                );
            }


            // =================================================
            // CASO 2
            // ANULADA → ACTIVA
            // =================================================

            if (
                    "ANULADA".equalsIgnoreCase(
                            estadoAnterior
                    )
                            &&
                            "ACTIVA".equalsIgnoreCase(
                                    estadoNuevo
                            )
            ) {

                aumentarInventario(
                        productoNuevoId,
                        cantidadNueva,
                        negocioId
                );
            }


            // =================================================
            // CASO 3
            // ACTIVA → ACTIVA
            // CAMBIO DE CANTIDAD
            // =================================================

            if (
                    "ACTIVA".equalsIgnoreCase(
                            estadoAnterior
                    )
                            &&
                            "ACTIVA".equalsIgnoreCase(
                                    estadoNuevo
                            )
            ) {

                int diferencia =
                        cantidadNueva
                                - cantidadAnterior;


                if (diferencia > 0) {

                    aumentarInventario(
                            productoNuevoId,
                            diferencia,
                            negocioId
                    );

                } else if (diferencia < 0) {

                    disminuirInventario(
                            productoNuevoId,
                            Math.abs(diferencia),
                            negocioId
                    );
                }
            }
        }


        // ====================================================
        // ACTUALIZAR CAMPOS
        // ====================================================

        actual.setCantidad(
                nueva.getCantidad()
        );

        actual.setMonto(
                nueva.getMonto()
        );

        actual.setMotivo(
                nueva.getMotivo()
        );

        actual.setEstado(
                nueva.getEstado()
        );


        /*
         * No permitimos cambiar la venta relacionada
         * desde una actualización simple.
         *
         * Esto evita alterar accidentalmente la relación
         * de la devolución con otra venta.
         */


        return repository.save(
                actual
        );
    }


    // ========================================================
    // 🔍 BUSCAR POR ID
    // ========================================================

    @Override
    public Devolucion buscarPorId(
            Integer id,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (id == null) {

            return null;
        }

        return repository
                .findByIdAndNegocioId(
                        id,
                        negocioId
                )
                .orElse(null);
    }


    // ========================================================
    // 🗑️ ELIMINAR
    // ========================================================

    @Override
    @Transactional
    public void eliminar(
            Integer id,
            Integer negocioId
    ) {

        validarNegocio(negocioId);


        // ====================================================
        // 🔐 SOLO PUEDE ELIMINAR UNA DEVOLUCIÓN DEL NEGOCIO
        // ====================================================

        Devolucion devolucion =
                buscarPorId(
                        id,
                        negocioId
                );


        if (devolucion == null) {

            return;
        }


        /*
         * Si la devolución estaba ACTIVA,
         * debemos revertir su efecto sobre el inventario.
         */

        if (
                "ACTIVA".equalsIgnoreCase(
                        devolucion.getEstado()
                )
        ) {

            if (devolucion.getProducto() != null) {

                disminuirInventario(
                        devolucion
                                .getProducto()
                                .getId(),

                        devolucion
                                .getCantidad(),

                        negocioId
                );
            }
        }


        // ====================================================
        // 🗑️ ELIMINAR
        // ====================================================

        repository.delete(
                devolucion
        );
    }


    // ========================================================
    // ✔️ VALIDAR DEVOLUCIÓN
    // ========================================================

    private void validarDevolucion(
            Devolucion devolucion
    ) {

        if (devolucion == null) {

            throw new RuntimeException(
                    "Los datos de la devolución son obligatorios."
            );
        }


        if (
                devolucion.getProducto() == null
        ) {

            throw new RuntimeException(
                    "Debe seleccionar un producto."
            );
        }


        if (
                devolucion.getCantidad() == null
                        ||
                        devolucion.getCantidad() <= 0
        ) {

            throw new RuntimeException(
                    "La cantidad debe ser mayor a cero."
            );
        }


        if (
                devolucion.getEstado() == null
                        ||
                        devolucion.getEstado().isBlank()
        ) {

            devolucion.setEstado(
                    "ACTIVA"
            );
        }
    }


    // ========================================================
    // 📦 AUMENTAR INVENTARIO
    // ========================================================

    private void aumentarInventario(
            Integer productoId,
            Integer cantidad,
            Integer negocioId
    ) {

        if (
                productoId == null ||
                        cantidad == null ||
                        cantidad <= 0
        ) {

            return;
        }


        /*
         * 🔐 IMPORTANTE:
         *
         * Ya no utilizamos:
         *
         * findByProductoId()
         *
         * porque esa consulta es global.
         *
         * Utilizamos:
         *
         * findByProductoIdAndNegocioId()
         */

        Inventario inventario =
                inventarioRepository
                        .findByProductoIdAndNegocioId(
                                productoId,
                                negocioId
                        )
                        .orElse(null);


        if (inventario == null) {

            return;
        }


        int stock =
                inventario.getStockActual() == null
                        ? 0
                        : inventario.getStockActual();


        inventario.setStockActual(
                stock + cantidad
        );


        inventarioRepository.save(
                inventario
        );
    }


    // ========================================================
    // 📦 DISMINUIR INVENTARIO
    // ========================================================

    private void disminuirInventario(
            Integer productoId,
            Integer cantidad,
            Integer negocioId
    ) {

        if (
                productoId == null ||
                        cantidad == null ||
                        cantidad <= 0
        ) {

            return;
        }


        /*
         * 🔐 CONSULTA AISLADA POR NEGOCIO
         */

        Inventario inventario =
                inventarioRepository
                        .findByProductoIdAndNegocioId(
                                productoId,
                                negocioId
                        )
                        .orElse(null);


        if (inventario == null) {

            return;
        }


        int stock =
                inventario.getStockActual() == null
                        ? 0
                        : inventario.getStockActual();


        stock =
                Math.max(
                        0,
                        stock - cantidad
                );


        inventario.setStockActual(
                stock
        );


        inventarioRepository.save(
                inventario
        );
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