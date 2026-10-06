// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.service;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Producto;
import com.ecomarket.model.Proveedor;
import com.ecomarket.repository.ProductoRepository;
import com.ecomarket.repository.ProveedorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ======================================================
 * 📦 SERVICE PRODUCTOS
 * ======================================================
 *
 * Responsabilidades:
 *
 * ✔ Listar productos del negocio
 * ✔ Buscar productos del negocio
 * ✔ Guardar productos
 * ✔ Buscar producto por ID
 * ✔ Actualizar productos
 * ✔ Eliminar producto de forma lógica
 * ✔ Validar pertenencia al negocio
 * ✔ Validar pertenencia del proveedor
 * ✔ Proteger las operaciones multiempresa
 *
 * ======================================================
 *
 * 🔐 SEGURIDAD MULTIEMPRESA
 * ======================================================
 *
 * Este Service recibe el negocioId desde el Controller.
 *
 * El negocioId debe provenir SIEMPRE del usuario autenticado
 * mediante CustomUserDetails.
 *
 * Nunca debemos confiar en un negocioId enviado por:
 *
 * - HTML
 * - JavaScript
 * - JSON
 * - Postman
 * - navegador
 *
 * Además:
 *
 * Producto y Proveedor deben pertenecer al MISMO negocio.
 *
 * ======================================================
 */
@Service
public class ProductoService {

    // =====================================================
    // 🔗 REPOSITORY PRODUCTOS
    // =====================================================

    @Autowired
    private ProductoRepository repository;


    // =====================================================
    // 🔗 REPOSITORY PROVEEDORES
    // =====================================================
    //
    // Se utiliza para verificar que el proveedor seleccionado
    // pertenezca al mismo negocio que el producto.
    //
    // =====================================================

    @Autowired
    private ProveedorRepository proveedorRepository;


    // =====================================================
    // 📋 LISTAR PRODUCTOS POR NEGOCIO
    // =====================================================

    /**
     * Obtiene únicamente los productos activos
     * pertenecientes al negocio autenticado.
     *
     * @param negocioId negocio del usuario autenticado
     * @return lista de productos del negocio
     */
    public List<Producto> listarPorNegocio(
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(negocioId);


        // -----------------------------------------------
        // 📋 CONSULTAR PRODUCTOS
        // -----------------------------------------------

        return repository
                .findByNegocioIdAndEstadoTrue(
                        negocioId
                );
    }


    // =====================================================
    // 🔍 BUSCAR PRODUCTOS POR NOMBRE
    // =====================================================

    /**
     * Busca productos por nombre dentro del negocio.
     *
     * Si el texto está vacío, devuelve todos los productos
     * activos del negocio.
     *
     * @param negocioId negocio del usuario autenticado
     * @param nombre nombre o texto de búsqueda
     * @return productos encontrados
     */
    public List<Producto> buscarPorNombre(
            Integer negocioId,
            String nombre
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(negocioId);


        // -----------------------------------------------
        // 🔎 SI NO HAY TEXTO, LISTAR TODO
        // -----------------------------------------------

        if (nombre == null || nombre.isBlank()) {

            return listarPorNegocio(
                    negocioId
            );
        }


        // -----------------------------------------------
        // 🔎 BUSCAR DENTRO DEL NEGOCIO
        // -----------------------------------------------

        return repository
                .findByNegocioIdAndEstadoTrueAndNombreContainingIgnoreCase(
                        negocioId,
                        nombre.trim()
                );
    }


    // =====================================================
    // 💾 GUARDAR PRODUCTO
    // =====================================================

    /**
     * Guarda un producto nuevo.
     *
     * 🔐 El negocioId se recibe desde el Controller y se
     * establece nuevamente aquí.
     *
     * Esto evita que un usuario pueda intentar registrar
     * un producto para otro negocio modificando el JSON.
     *
     * 🔐 También se valida que el proveedor pertenezca
     * al mismo negocio.
     *
     * @param producto datos del producto
     * @param negocioId negocio del usuario autenticado
     * @return producto guardado
     */
    public Producto guardar(
            Producto producto,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(negocioId);


        // -----------------------------------------------
        // 🛑 VALIDAR OBJETO
        // -----------------------------------------------

        if (producto == null) {

            throw new IllegalArgumentException(
                    "Los datos del producto son obligatorios."
            );
        }


        // -----------------------------------------------
        // ❌ VALIDAR NOMBRE
        // -----------------------------------------------

        if (producto.getNombre() == null ||
                producto.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio."
            );
        }


        // -----------------------------------------------
        // ❌ VALIDAR PRECIO
        // -----------------------------------------------

        if (producto.getPrecio() == null ||
                producto.getPrecio() <= 0) {

            throw new IllegalArgumentException(
                    "El precio del producto debe ser mayor que cero."
            );
        }


        // -----------------------------------------------
        // 🔐 FORZAR NEGOCIO
        // -----------------------------------------------
        //
        // Nunca utilizamos el negocioId que pueda venir
        // desde el frontend.
        //
        // Utilizamos exclusivamente el negocioId recibido
        // desde el usuario autenticado.
        //
        // -----------------------------------------------

        producto.setNegocioId(
                negocioId
        );


        // -----------------------------------------------
        // ✂ LIMPIAR NOMBRE
        // -----------------------------------------------

        producto.setNombre(
                producto.getNombre().trim()
        );


        // -----------------------------------------------
        // 🔐 VALIDAR PROVEEDOR
        // -----------------------------------------------
        //
        // Si el producto tiene proveedor, verificamos que
        // dicho proveedor pertenezca al mismo negocio.
        //
        // Esto evita:
        //
        // Negocio A
        //     ↓
        // Producto A
        //     ↓
        // Proveedor del negocio B ❌
        //
        // -----------------------------------------------

        if (producto.getProveedor() != null &&
                producto.getProveedor().getId() != null) {

            Proveedor proveedor =
                    proveedorRepository
                            .findByIdAndNegocioId(
                                    producto.getProveedor().getId(),
                                    negocioId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "El proveedor no existe o no pertenece a este negocio."
                                    )
                            );


            // -------------------------------------------
            // 🔐 USAR ENTIDAD REAL DE LA BASE DE DATOS
            // -------------------------------------------
            //
            // No guardamos directamente el objeto enviado
            // por el navegador.
            //
            // Utilizamos el proveedor validado por JPA.
            //
            // -------------------------------------------

            producto.setProveedor(
                    proveedor
            );
        }


        // -----------------------------------------------
        // ❌ PROVEEDOR SIN ID
        // -----------------------------------------------

        else if (producto.getProveedor() != null) {

            throw new IllegalArgumentException(
                    "El proveedor seleccionado no es válido."
            );
        }


        // -----------------------------------------------
        // ✅ ESTADO POR DEFECTO
        // -----------------------------------------------

        if (producto.getEstado() == null) {

            producto.setEstado(true);
        }


        // -----------------------------------------------
        // 💾 GUARDAR
        // -----------------------------------------------

        return repository.save(
                producto
        );
    }


    // =====================================================
    // 🔍 BUSCAR PRODUCTO POR ID
    // =====================================================

    /**
     * Busca un producto por ID, pero únicamente dentro
     * del negocio autenticado.
     *
     * @param id identificador del producto
     * @param negocioId negocio autenticado
     * @return producto o null si no pertenece al negocio
     */
    public Producto buscarPorId(
            Integer id,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(negocioId);


        // -----------------------------------------------
        // ❌ VALIDAR ID
        // -----------------------------------------------

        if (id == null) {

            return null;
        }


        // -----------------------------------------------
        // 🔐 BUSCAR POR ID + NEGOCIO
        // -----------------------------------------------

        return repository
                .findByIdAndNegocioId(
                        id,
                        negocioId
                )
                .orElse(null);
    }


    // =====================================================
    // ✏ ACTUALIZAR PRODUCTO
    // =====================================================

    /**
     * Actualiza un producto existente.
     *
     * 🔐 Primero verifica que el producto pertenezca al
     * negocio autenticado.
     *
     * 🔐 También verifica que el nuevo proveedor pertenezca
     * al mismo negocio.
     *
     * El negocioId NO se modifica.
     *
     * @param id ID del producto
     * @param datos nuevos datos
     * @param negocioId negocio autenticado
     * @return producto actualizado o null
     */
    public Producto actualizar(
            Integer id,
            Producto datos,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(negocioId);


        // -----------------------------------------------
        // 🛑 VALIDAR DATOS
        // -----------------------------------------------

        if (datos == null) {

            throw new IllegalArgumentException(
                    "Los datos del producto son obligatorios."
            );
        }


        // -----------------------------------------------
        // 🔐 BUSCAR PRODUCTO DENTRO DEL NEGOCIO
        // -----------------------------------------------

        Producto producto =
                repository
                        .findByIdAndNegocioId(
                                id,
                                negocioId
                        )
                        .orElse(null);


        // -----------------------------------------------
        // ❌ PRODUCTO NO ENCONTRADO
        // -----------------------------------------------

        if (producto == null) {

            return null;
        }


        // -----------------------------------------------
        // ❌ VALIDAR NOMBRE
        // -----------------------------------------------

        if (datos.getNombre() == null ||
                datos.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio."
            );
        }


        // -----------------------------------------------
        // ❌ VALIDAR PRECIO
        // -----------------------------------------------

        if (datos.getPrecio() == null ||
                datos.getPrecio() <= 0) {

            throw new IllegalArgumentException(
                    "El precio del producto debe ser mayor que cero."
            );
        }


        // -----------------------------------------------
        // ✂ LIMPIAR NOMBRE
        // -----------------------------------------------

        producto.setNombre(
                datos.getNombre().trim()
        );


        // =================================================
        // 🔥 ACTUALIZAR CAMPOS PERMITIDOS
        // =================================================

        producto.setDescripcion(
                datos.getDescripcion()
        );

        producto.setPrecio(
                datos.getPrecio()
        );


        // =================================================
        // 🔐 VALIDAR PROVEEDOR
        // =================================================
        //
        // El proveedor nuevo debe pertenecer al mismo
        // negocio del producto.
        //
        // =================================================

        if (datos.getProveedor() != null &&
                datos.getProveedor().getId() != null) {

            Proveedor proveedor =
                    proveedorRepository
                            .findByIdAndNegocioId(
                                    datos.getProveedor().getId(),
                                    negocioId
                            )
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "El proveedor no existe o no pertenece a este negocio."
                                    )
                            );


            // -------------------------------------------
            // 🔐 ASIGNAR PROVEEDOR VALIDADO
            // -------------------------------------------

            producto.setProveedor(
                    proveedor
            );
        }

        else if (datos.getProveedor() != null) {

            throw new IllegalArgumentException(
                    "El proveedor seleccionado no es válido."
            );
        }

        else {

            // -------------------------------------------
            // ℹ PROVEEDOR NO SELECCIONADO
            // -------------------------------------------
            //
            // Permitimos que el producto quede sin proveedor
            // si el formulario permite este comportamiento.
            //
            producto.setProveedor(null);
        }


        // =================================================
        // 🔐 NO MODIFICAR NEGOCIO
        // =================================================
        //
        // El producto ya pertenece a este negocio.
        //
        // Nunca hacemos:
        //
        // producto.setNegocioId(
        //      datos.getNegocioId()
        // );
        //
        // =================================================


        // -----------------------------------------------
        // 💾 GUARDAR CAMBIOS
        // -----------------------------------------------

        return repository.save(
                producto
        );
    }


    // =====================================================
    // 📉 DESCONTAR STOCK
    // =====================================================

    /**
     * Valida que el producto pertenezca al negocio.
     *
     * ⚠️ El stock real NO se modifica aquí porque la entidad
     * Producto no administra stock.
     *
     * El stock está en Inventario.
     *
     * La modificación real debe hacerse mediante
     * InventarioService.
     *
     * @param productoId ID del producto
     * @param cantidad cantidad a descontar
     * @param negocioId negocio autenticado
     */
    public void descontarStock(
            Integer productoId,
            Integer cantidad,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(negocioId);


        // -----------------------------------------------
        // ❌ CANTIDAD INVÁLIDA
        // -----------------------------------------------

        if (cantidad == null || cantidad <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad a descontar debe ser mayor que cero."
            );
        }


        // -----------------------------------------------
        // 🔐 VALIDAR PRODUCTO + NEGOCIO
        // -----------------------------------------------

        repository
                .findByIdAndNegocioId(
                        productoId,
                        negocioId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado en este negocio."
                        )
                );


        // -----------------------------------------------
        // ⚠️ NO MODIFICAR STOCK AQUÍ
        // -----------------------------------------------
        //
        // El stock pertenece a Inventario.
        //
        // InventarioService será responsable de realizar
        // el descuento real.
        //
    }


    // =====================================================
    // ➕ AUMENTAR STOCK
    // =====================================================

    /**
     * Valida que el producto pertenezca al negocio.
     *
     * El aumento real del stock debe realizarse en
     * InventarioService.
     *
     * @param productoId ID del producto
     * @param cantidad cantidad a aumentar
     * @param negocioId negocio autenticado
     */
    public void aumentarStock(
            Integer productoId,
            Integer cantidad,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(negocioId);


        // -----------------------------------------------
        // ❌ CANTIDAD INVÁLIDA
        // -----------------------------------------------

        if (cantidad == null || cantidad <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad a aumentar debe ser mayor que cero."
            );
        }


        // -----------------------------------------------
        // 🔐 VALIDAR PRODUCTO + NEGOCIO
        // -----------------------------------------------

        repository
                .findByIdAndNegocioId(
                        productoId,
                        negocioId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado en este negocio."
                        )
                );


        // -----------------------------------------------
        // ⚠️ NO MODIFICAR STOCK AQUÍ
        // -----------------------------------------------
        //
        // El stock real está en Inventario.
        //
        // La modificación debe realizarse mediante
        // InventarioService.
        //
    }


    // =====================================================
    // ❌ ELIMINAR LÓGICO
    // =====================================================

    /**
     * Desactiva un producto.
     *
     * 🔐 Solamente puede desactivarse un producto que
     * pertenezca al negocio autenticado.
     *
     * No se elimina físicamente de la base de datos.
     *
     * @param id ID del producto
     * @param negocioId negocio autenticado
     * @return true si fue eliminado, false si no existe
     */
    public boolean eliminar(
            Integer id,
            Integer negocioId
    ) {

        // -----------------------------------------------
        // 🔐 VALIDAR NEGOCIO
        // -----------------------------------------------

        validarNegocio(negocioId);


        // -----------------------------------------------
        // 🔐 BUSCAR PRODUCTO DENTRO DEL NEGOCIO
        // -----------------------------------------------

        Producto producto =
                repository
                        .findByIdAndNegocioId(
                                id,
                                negocioId
                        )
                        .orElse(null);


        // -----------------------------------------------
        // ❌ NO EXISTE O NO PERTENECE AL NEGOCIO
        // -----------------------------------------------

        if (producto == null) {

            return false;
        }


        // -----------------------------------------------
        // 📉 ELIMINACIÓN LÓGICA
        // -----------------------------------------------

        producto.setEstado(false);


        // -----------------------------------------------
        // 💾 GUARDAR
        // -----------------------------------------------

        repository.save(
                producto
        );


        return true;
    }


    // =====================================================
    // 🔐 VALIDAR NEGOCIO
    // =====================================================

    /**
     * Comprueba que exista un negocio asociado al usuario.
     *
     * @param negocioId negocio autenticado
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

