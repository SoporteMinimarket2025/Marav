package com.ecomarket.service.impl;

import com.ecomarket.model.Proveedor;
import com.ecomarket.repository.ProveedorRepository;
import com.ecomarket.service.ProveedorService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ============================================================
 * IMPLEMENTACIÓN DEL SERVICIO DE PROVEEDORES
 * ============================================================
 *
 * Aquí se aplican las reglas de negocio relacionadas
 * con los proveedores.
 *
 * IMPORTANTE:
 *
 * Todas las operaciones utilizan negocioId.
 *
 * Esto evita que un usuario de un negocio pueda:
 *
 * - Ver proveedores de otro negocio.
 * - Buscar proveedores de otro negocio.
 * - Consultar un proveedor de otro negocio.
 * - Eliminar un proveedor de otro negocio.
 *
 * El negocioId será obtenido posteriormente desde
 * CustomUserDetails en el ProveedorController.
 * ============================================================
 */
@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl
        implements ProveedorService {

    private final ProveedorRepository proveedorRepository;


    // =========================================================
    // LISTAR PROVEEDORES DEL NEGOCIO
    // =========================================================

    @Override
    public List<Proveedor> listar(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        return proveedorRepository
                .findByNegocioId(
                        negocioId
                );
    }


    // =========================================================
    // GUARDAR PROVEEDOR
    // =========================================================

    @Override
    public Proveedor guardar(
            Proveedor proveedor,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (proveedor == null) {

            throw new IllegalArgumentException(
                    "El proveedor no puede ser nulo."
            );
        }

        if (proveedor.getNombre() == null
                || proveedor.getNombre().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El nombre del proveedor es obligatorio."
            );
        }

        /*
         * IMPORTANTE:
         *
         * El negocioId NO debe venir desde el frontend.
         *
         * Siempre lo asignamos desde el usuario autenticado.
         */
        proveedor.setNegocioId(
                negocioId
        );

        return proveedorRepository.save(
                proveedor
        );
    }


    // =========================================================
    // BUSCAR PROVEEDOR POR ID
    // =========================================================

    @Override
    public Proveedor buscarPorId(
            Integer id,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (id == null) {

            throw new IllegalArgumentException(
                    "El ID del proveedor es obligatorio."
            );
        }

        /*
         * MUY IMPORTANTE:
         *
         * No usamos:
         *
         * proveedorRepository.findById(id)
         *
         * porque eso permitiría encontrar registros
         * pertenecientes a cualquier negocio.
         *
         * Utilizamos la consulta filtrada por negocio.
         */
        return proveedorRepository
                .findByIdAndNegocioId(
                        id,
                        negocioId
                )
                .orElse(null);
    }


    // =========================================================
    // ELIMINAR PROVEEDOR
    // =========================================================

    @Override
    public void eliminar(
            Integer id,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (id == null) {

            throw new IllegalArgumentException(
                    "El ID del proveedor es obligatorio."
            );
        }

        /*
         * Primero verificamos que el proveedor
         * realmente pertenezca al negocio.
         */
        Proveedor proveedor =
                proveedorRepository
                        .findByIdAndNegocioId(
                                id,
                                negocioId
                        )
                        .orElse(null);

        if (proveedor == null) {

            throw new IllegalArgumentException(
                    "El proveedor no existe "
                            + "o no pertenece a este negocio."
            );
        }

        proveedorRepository.delete(
                proveedor
        );
    }


    // =========================================================
    // BUSCAR POR NOMBRE
    // =========================================================

    @Override
    public List<Proveedor> buscar(
            String nombre,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        /*
         * Si la búsqueda está vacía, devolvemos
         * todos los proveedores del negocio.
         */
        if (nombre == null
                || nombre.trim().isEmpty()) {

            return listar(
                    negocioId
            );
        }

        return proveedorRepository
                .findByNegocioIdAndNombreContainingIgnoreCase(
                        negocioId,
                        nombre.trim()
                );
    }


    // =========================================================
    // VALIDAR NEGOCIO
    // =========================================================

    /**
     * Verifica que el usuario autenticado tenga
     * un negocio asociado.
     *
     * Esto evita ejecutar consultas sin negocioId.
     */
    private void validarNegocio(
            Integer negocioId
    ) {

        if (negocioId == null) {

            throw new IllegalStateException(
                    "El usuario autenticado "
                            + "no tiene un negocio asociado."
            );
        }
    }
}

