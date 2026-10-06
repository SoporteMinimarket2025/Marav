
// ======================================================
// 📁 PACKAGE
// ======================================================
        package com.ecomarket.service;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.dto.ClienteDTO;
import com.ecomarket.model.Cliente;
import com.ecomarket.repository.ClienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ======================================================
 * 👥 SERVICE CLIENTES
 * ======================================================
 *
 * 🔐 Este Service trabaja siempre con negocioId.
 *
 * Regla:
 *
 * USUARIO LOGUEADO
 *       ↓
 * negocioId
 *       ↓
 * ClienteService
 *       ↓
 * ClienteRepository
 *       ↓
 * SOLO DATOS DEL NEGOCIO
 *
 * ======================================================
 */
@Service
public class ClienteService {

    // ==================================================
    // 🔗 REPOSITORY
    // ==================================================
    @Autowired
    private ClienteRepository repository;


    // ==================================================
    // 📋 LISTAR CLIENTES
    // ==================================================
    public List<Cliente> listar(
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        return repository.findByNegocioIdAndEstadoTrue(
                negocioId
        );
    }


    // ==================================================
    // 🔍 BUSCAR CLIENTES
    // ==================================================
    public List<Cliente> buscar(
            Integer negocioId,
            String nombre
    ) {

        validarNegocio(negocioId);

        // ==============================================
        // Si no se escribe nada, devolver todos
        // los clientes del negocio.
        // ==============================================
        if (nombre == null || nombre.isBlank()) {

            return listar(negocioId);
        }

        return repository
                .findByNegocioIdAndNombreContainingIgnoreCaseAndEstadoTrue(
                        negocioId,
                        nombre
                );
    }


    // ==================================================
    // 💾 GUARDAR CLIENTE
    // ==================================================
    public Cliente guardar(
            ClienteDTO dto,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        // ==============================================
        // ❌ VALIDAR NOMBRE
        // ==============================================
        if (dto.getNombre() == null
                || dto.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre es obligatorio"
            );
        }

        // ==============================================
        // 🔥 CREAR CLIENTE
        // ==============================================
        Cliente cliente = new Cliente();

        cliente.setNombre(
                dto.getNombre()
        );

        cliente.setTipoIdentificacion(
                dto.getTipoIdentificacion()
        );

        cliente.setNumeroIdentificacion(
                dto.getNumeroIdentificacion()
        );

        cliente.setTelefono(
                dto.getTelefono()
        );

        cliente.setDireccion(
                dto.getDireccion()
        );

        // =================================================
        // 🔐 ASIGNAR NEGOCIO DESDE EL BACKEND
        // =================================================
        //
        // NO viene del HTML.
        // NO viene del JSON.
        // NO viene del usuario.
        //
        // Se obtiene del usuario autenticado.
        //
        cliente.setNegocioId(
                negocioId
        );

        return repository.save(
                cliente
        );
    }


    // ==================================================
    // 🔍 BUSCAR POR ID
    // ==================================================
    public Cliente buscarPorId(
            Integer id,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        return repository
                .findByIdAndNegocioId(
                        id,
                        negocioId
                )
                .orElse(null);
    }


    // ==================================================
    // ✏ ACTUALIZAR
    // ==================================================
    public Cliente actualizar(
            Integer id,
            ClienteDTO dto,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        // =================================================
        // 🔐 BUSCAR SOLO DENTRO DEL NEGOCIO
        // =================================================
        Cliente cliente =
                repository
                        .findByIdAndNegocioId(
                                id,
                                negocioId
                        )
                        .orElse(null);

        if (cliente == null) {
            return null;
        }

        // =================================================
        // ❌ VALIDAR NOMBRE
        // =================================================
        if (dto.getNombre() == null
                || dto.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre es obligatorio"
            );
        }

        // =================================================
        // 🔥 ACTUALIZAR
        // =================================================
        cliente.setNombre(
                dto.getNombre()
        );

        cliente.setTipoIdentificacion(
                dto.getTipoIdentificacion()
        );

        cliente.setNumeroIdentificacion(
                dto.getNumeroIdentificacion()
        );

        cliente.setTelefono(
                dto.getTelefono()
        );

        cliente.setDireccion(
                dto.getDireccion()
        );

        // =================================================
        // 🔐 NO CAMBIAMOS negocioId
        // =================================================
        //
        // Esto evita que un cliente pueda ser movido
        // accidentalmente a otro negocio.
        //
        return repository.save(
                cliente
        );
    }


    // ==================================================
    // ❌ ELIMINAR LÓGICO
    // ==================================================
    public void eliminar(
            Integer id,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        // =================================================
        // 🔐 SOLO BUSCA DENTRO DEL NEGOCIO
        // =================================================
        Cliente cliente =
                repository
                        .findByIdAndNegocioId(
                                id,
                                negocioId
                        )
                        .orElse(null);

        if (cliente != null) {

            cliente.setEstado(false);

            repository.save(
                    cliente
            );
        }
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

