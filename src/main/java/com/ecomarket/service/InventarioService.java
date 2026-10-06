// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.service;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Inventario;
import com.ecomarket.model.Producto;
import com.ecomarket.repository.InventarioRepository;
import com.ecomarket.repository.ProductoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

// ======================================================
// 🚀 SERVICE INVENTARIO (PRO)
// ======================================================

/**
 * ======================================================
 * 📦 SERVICIO DE INVENTARIO
 * ======================================================
 *
 * Responsabilidades:
 *
 * ✔ Listar inventario
 * ✔ Crear inventario
 * ✔ Consultar inventario
 * ✔ Actualizar inventario
 * ✔ Eliminar inventario
 * ✔ Detectar stock bajo
 * ✔ Aumentar stock
 * ✔ Validar productos
 * ✔ Aislamiento por negocio
 *
 * ======================================================
 *
 * 🔐 REGLA MULTI-NEGOCIO
 *
 * La entidad Inventario actualmente NO tiene un
 * negocioId propio.
 *
 * Por esta razón, la pertenencia al negocio se determina
 * mediante:
 *
 * Inventario
 *     ↓
 * productoId
 *     ↓
 * Producto
 *     ↓
 * negocioId
 *
 * ======================================================
 *
 * Esto significa que antes de cualquier operación sobre
 * inventario verificamos que el producto pertenezca al
 * negocio autenticado.
 *
 * ======================================================
 */

@Service
public class InventarioService {


    // =====================================================
    // 🔗 REPOSITORIES
    // =====================================================

    private final InventarioRepository repository;

    private final ProductoRepository productoRepository;


    // =====================================================
    // 🏗 CONSTRUCTOR
    // =====================================================

    public InventarioService(
            InventarioRepository repository,
            ProductoRepository productoRepository
    ) {

        this.repository =
                repository;

        this.productoRepository =
                productoRepository;
    }


    // =====================================================
    // 📋 LISTAR INVENTARIO
    // =====================================================

    /**
     * Lista únicamente el inventario correspondiente
     * al negocio autenticado.
     *
     * @param negocioId negocio del usuario autenticado
     * @return inventario del negocio
     */

    public List<Inventario> listar(
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(
                negocioId
        );


        // -----------------------------------------------
        // 📋 CONSULTAR INVENTARIO
        // -----------------------------------------------

        return repository.findAllByNegocioId(
                negocioId
        );
    }


    // =====================================================
    // 💾 GUARDAR INVENTARIO
    // =====================================================

    /**
     * Crea un registro de inventario.
     *
     * Antes de guardar:
     *
     * ✔ valida negocio
     * ✔ valida datos
     * ✔ verifica que el producto exista
     * ✔ verifica que pertenezca al negocio
     * ✔ verifica que esté activo
     * ✔ evita inventarios duplicados
     */

    public Inventario guardar(
            Inventario inventario,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(
                negocioId
        );


        // -----------------------------------------------
        // 🔐 VALIDAR INVENTARIO
        // -----------------------------------------------

        validarInventario(
                inventario
        );


        // =================================================
        // 🔐 VERIFICAR PRODUCTO + NEGOCIO
        // =================================================

        Producto producto =
                productoRepository
                        .findByIdAndNegocioId(
                                inventario.getProductoId(),
                                negocioId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El producto seleccionado no pertenece a este negocio."
                                )
                        );


        // =================================================
        // 🚫 VERIFICAR PRODUCTO ACTIVO
        // =================================================

        if (
                producto.getEstado() != null &&
                        !producto.getEstado()
        ) {

            throw new IllegalArgumentException(
                    "No se puede crear inventario para un producto inactivo."
            );
        }


        // =================================================
        // 🚫 EVITAR INVENTARIO DUPLICADO
        // =================================================

        if (
                repository.existsByProductoIdAndNegocioId(
                        inventario.getProductoId(),
                        negocioId
                )
        ) {

            throw new IllegalArgumentException(
                    "El producto ya tiene un registro de inventario."
            );
        }


        // =================================================
        // 💾 GUARDAR
        // =================================================

        return repository.save(
                inventario
        );
    }


    // =====================================================
    // 🔍 BUSCAR INVENTARIO POR ID
    // =====================================================

    /**
     * Busca inventario por ID + negocio.
     *
     * Un usuario de otro negocio no podrá obtener
     * registros que no le pertenecen.
     */

    public Inventario buscarPorId(
            Integer id,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(
                negocioId
        );


        // -----------------------------------------------
        // ❌ VALIDAR ID
        // -----------------------------------------------

        if (id == null) {

            return null;
        }


        // -----------------------------------------------
        // 🔐 BUSCAR ID + NEGOCIO
        // -----------------------------------------------

        return repository
                .findByIdAndNegocioId(
                        id,
                        negocioId
                )
                .orElse(null);
    }


    // =====================================================
    // ✏ ACTUALIZAR INVENTARIO
    // =====================================================

    /**
     * Actualiza un registro de inventario.
     *
     * También permite cambiar el producto asociado,
     * siempre que el nuevo producto:
     *
     * ✔ pertenezca al negocio
     * ✔ esté activo
     * ✔ no tenga otro inventario asociado
     */

    public Inventario actualizar(
            Integer id,
            Inventario inventario,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(
                negocioId
        );


        // -----------------------------------------------
        // 🔍 BUSCAR INVENTARIO ACTUAL
        // -----------------------------------------------

        Inventario actual =
                buscarPorId(
                        id,
                        negocioId
                );


        // -----------------------------------------------
        // ❌ NO EXISTE
        // -----------------------------------------------

        if (actual == null) {

            return null;
        }


        // -----------------------------------------------
        // 🔐 VALIDAR DATOS
        // -----------------------------------------------

        validarInventario(
                inventario
        );


        // =================================================
        // 🔐 VERIFICAR PRODUCTO + NEGOCIO
        // =================================================

        Producto producto =
                productoRepository
                        .findByIdAndNegocioId(
                                inventario.getProductoId(),
                                negocioId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El producto seleccionado no pertenece a este negocio."
                                )
                        );


        // =================================================
        // 🚫 VERIFICAR PRODUCTO ACTIVO
        // =================================================

        if (
                producto.getEstado() != null &&
                        !producto.getEstado()
        ) {

            throw new IllegalArgumentException(
                    "No se puede asociar inventario a un producto inactivo."
            );
        }


        // =================================================
        // 🔄 VERIFICAR SI CAMBIÓ EL PRODUCTO
        // =================================================

        if (
                !actual.getProductoId()
                        .equals(
                                inventario.getProductoId()
                        )
        ) {

            // ---------------------------------------------
            // 🚫 EL NUEVO PRODUCTO YA TIENE INVENTARIO
            // ---------------------------------------------

            if (
                    repository.existsByProductoIdAndNegocioId(
                            inventario.getProductoId(),
                            negocioId
                    )
            ) {

                throw new IllegalArgumentException(
                        "El producto seleccionado ya tiene inventario."
                );
            }
        }


        // =================================================
        // 🔥 ACTUALIZAR CAMPOS
        // =================================================

        actual.setProductoId(
                inventario.getProductoId()
        );

        actual.setStockActual(
                inventario.getStockActual()
        );

        actual.setStockMinimo(
                inventario.getStockMinimo()
        );

        actual.setUbicacion(
                inventario.getUbicacion()
        );

        actual.setLote(
                inventario.getLote()
        );

        actual.setFechaVencimiento(
                inventario.getFechaVencimiento()
        );


        // =================================================
        // 💾 GUARDAR CAMBIOS
        // =================================================

        return repository.save(
                actual
        );
    }


    // =====================================================
    // ❌ ELIMINAR INVENTARIO
    // =====================================================

    /**
     * Elimina físicamente el registro de inventario,
     * siempre verificando primero que pertenezca al
     * negocio autenticado.
     */

    public void eliminar(
            Integer id,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(
                negocioId
        );


        // -----------------------------------------------
        // 🔍 BUSCAR INVENTARIO
        // -----------------------------------------------

        Inventario inventario =
                buscarPorId(
                        id,
                        negocioId
                );


        // -----------------------------------------------
        // ❌ NO EXISTE
        // -----------------------------------------------

        if (inventario == null) {

            throw new IllegalArgumentException(
                    "El inventario no existe o no pertenece a este negocio."
            );
        }


        // -----------------------------------------------
        // 🗑 ELIMINAR
        // -----------------------------------------------

        repository.delete(
                inventario
        );
    }


    // =====================================================
    // 📉 OBTENER STOCK BAJO
    // =====================================================

    /**
     * Obtiene los productos cuyo stock actual es menor
     * o igual al stock mínimo configurado.
     *
     * La consulta ya está filtrada por negocio en el
     * InventarioRepository.
     */

    public List<Inventario> stockBajo(
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(
                negocioId
        );


        // -----------------------------------------------
        // 📉 CONSULTAR STOCK BAJO
        // -----------------------------------------------

        return repository.obtenerStockBajo(
                negocioId
        );
    }


    // =====================================================
    // ➕ AUMENTAR STOCK
    // =====================================================

    /**
     * Aumenta el stock actual de un producto.
     *
     * Primero verifica:
     *
     * ✔ negocio válido
     * ✔ cantidad válida
     * ✔ producto perteneciente al negocio
     * ✔ inventario perteneciente al negocio
     */

    public void aumentarStock(
            Integer productoId,
            Integer cantidad,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(
                negocioId
        );


        // -----------------------------------------------
        // ❌ VALIDAR DATOS
        // -----------------------------------------------

        if (
                productoId == null
        ) {

            throw new IllegalArgumentException(
                    "El producto es obligatorio."
            );
        }


        if (
                cantidad == null ||
                        cantidad <= 0
        ) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }


        // =================================================
        // 🔐 VALIDAR PRODUCTO + NEGOCIO
        // =================================================

        productoRepository
                .findByIdAndNegocioId(
                        productoId,
                        negocioId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El producto no pertenece a este negocio."
                        )
                );


        // =================================================
        // 🔍 BUSCAR INVENTARIO
        // =================================================

        Inventario inventario =
                repository
                        .findByProductoIdAndNegocioId(
                                productoId,
                                negocioId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "El producto no tiene un registro de inventario."
                                )
                        );


        // =================================================
        // 📦 OBTENER STOCK ACTUAL
        // =================================================

        int stock =
                inventario.getStockActual() == null
                        ? 0
                        : inventario.getStockActual();


        // =================================================
        // ➕ AUMENTAR STOCK
        // =================================================

        inventario.setStockActual(
                stock + cantidad
        );


        // =================================================
        // 💾 GUARDAR
        // =================================================

        repository.save(
                inventario
        );
    }


    // =====================================================
    // 🔐 VALIDAR INVENTARIO
    // =====================================================

    /**
     * Valida los datos básicos del inventario.
     */

    private void validarInventario(
            Inventario inventario
    ) {

        // -----------------------------------------------
        // ❌ OBJETO NULO
        // -----------------------------------------------

        if (inventario == null) {

            throw new IllegalArgumentException(
                    "Los datos del inventario son obligatorios."
            );
        }


        // -----------------------------------------------
        // ❌ PRODUCTO OBLIGATORIO
        // -----------------------------------------------

        if (
                inventario.getProductoId() == null
        ) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un producto."
            );
        }


        // -----------------------------------------------
        // ❌ STOCK ACTUAL
        // -----------------------------------------------

        if (
                inventario.getStockActual() == null ||
                        inventario.getStockActual() < 0
        ) {

            throw new IllegalArgumentException(
                    "El stock actual no puede ser negativo."
            );
        }


        // -----------------------------------------------
        // ❌ STOCK MÍNIMO
        // -----------------------------------------------

        if (
                inventario.getStockMinimo() == null ||
                        inventario.getStockMinimo() < 0
        ) {

            throw new IllegalArgumentException(
                    "El stock mínimo no puede ser negativo."
            );
        }
    }


    // =====================================================
    // 🔐 VALIDAR NEGOCIO
    // =====================================================

    /**
     * Comprueba que el usuario tenga un negocio asociado.
     */

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

