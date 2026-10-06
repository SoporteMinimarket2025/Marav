// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.service;

// ======================================================
// 📚 IMPORTS
// ======================================================
import com.ecomarket.model.Caja;
import com.ecomarket.repository.CajaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ======================================================
 * 💰 SERVICIO CAJA
 * ======================================================
 *
 * Este Service aplica el aislamiento por negocio.
 *
 * El negocio NO viene desde el frontend.
 *
 * El Controller obtiene el negocioId desde:
 *
 * CustomUserDetails
 *
 * y lo envía aquí.
 *
 * ======================================================
 */
@Service
public class CajaService {


    // ==================================================
    // 📦 REPOSITORY
    // ==================================================

    @Autowired
    private CajaRepository cajaRepository;


    // ==================================================
    // 📋 LISTAR CAJAS
    // ==================================================

    public List<Caja> listar(Integer negocioId) {

        validarNegocio(negocioId);

        return cajaRepository
                .findAllByNegocioIdOrderByFechaAperturaDesc(
                        negocioId
                );
    }


    // ==================================================
    // 💰 OBTENER CAJA ABIERTA
    // ==================================================

    public Caja obtenerCajaAbierta(Integer negocioId) {

        validarNegocio(negocioId);

        List<Caja> cajas =
                cajaRepository.buscarCajaAbierta(
                        negocioId
                );

        if (cajas == null || cajas.isEmpty()) {
            return null;
        }

        return cajas.get(0);
    }


    // ==================================================
    // 🔎 BUSCAR CAJA POR ID
    // ==================================================

    public Caja buscarPorId(
            Integer id,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (id == null) {
            return null;
        }

        return cajaRepository
                .findByIdAndNegocioId(
                        id,
                        negocioId
                )
                .orElse(null);
    }


    // ==================================================
    // 🔓 ABRIR CAJA
    // ==================================================

    public Caja abrirCaja(
            Integer usuarioId,
            Double saldoInicial,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        if (usuarioId == null) {

            throw new RuntimeException(
                    "No se pudo identificar el usuario."
            );
        }


        // ==================================================
        // 🔒 COMPROBAR CAJA ABIERTA DEL MISMO NEGOCIO
        // ==================================================

        Caja abierta =
                obtenerCajaAbierta(
                        negocioId
                );

        if (abierta != null) {

            throw new RuntimeException(
                    "Ya existe una caja abierta para este negocio."
            );
        }


        // ==================================================
        // 💰 VALIDAR SALDO INICIAL
        // ==================================================

        if (saldoInicial == null) {

            saldoInicial = 0.0;
        }

        if (saldoInicial < 0) {

            throw new RuntimeException(
                    "El saldo inicial no puede ser negativo."
            );
        }


        // ==================================================
        // 🧾 CREAR CAJA
        // ==================================================

        Caja caja = new Caja();

        caja.setIdUsuario(
                usuarioId
        );

        caja.setSaldoInicial(
                saldoInicial
        );

        caja.setIngresos(
                0.0
        );

        caja.setEgresos(
                0.0
        );

        caja.setSaldoFinal(
                saldoInicial
        );

        caja.setEstado(
                "ABIERTA"
        );

        caja.setFechaApertura(
                LocalDateTime.now()
        );


        // ==================================================
        // 💾 GUARDAR
        // ==================================================

        return cajaRepository.save(
                caja
        );
    }


    // ==================================================
    // 💵 REGISTRAR INGRESO
    // ==================================================

    public Caja registrarIngreso(
            Double valor,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        validarValor(
                valor
        );


        // ==================================================
        // 🔎 OBTENER CAJA DEL MISMO NEGOCIO
        // ==================================================

        Caja caja =
                obtenerCajaAbierta(
                        negocioId
                );

        if (caja == null) {

            throw new RuntimeException(
                    "No existe una caja abierta para este negocio."
            );
        }


        // ==================================================
        // 💵 SUMAR INGRESO
        // ==================================================

        double ingresosActuales =
                caja.getIngresos() != null
                        ? caja.getIngresos()
                        : 0.0;

        caja.setIngresos(
                ingresosActuales + valor
        );


        // ==================================================
        // 💰 ACTUALIZAR SALDO
        // ==================================================

        actualizarSaldo(
                caja
        );


        // ==================================================
        // 💾 GUARDAR
        // ==================================================

        return cajaRepository.save(
                caja
        );
    }


    // ==================================================
    // 💸 REGISTRAR EGRESO
    // ==================================================

    public Caja registrarEgreso(
            Double valor,
            Integer negocioId
    ) {

        validarNegocio(negocioId);

        validarValor(
                valor
        );


        // ==================================================
        // 🔎 OBTENER CAJA DEL MISMO NEGOCIO
        // ==================================================

        Caja caja =
                obtenerCajaAbierta(
                        negocioId
                );

        if (caja == null) {

            throw new RuntimeException(
                    "No existe una caja abierta para este negocio."
            );
        }


        // ==================================================
        // 💸 SUMAR EGRESO
        // ==================================================

        double egresosActuales =
                caja.getEgresos() != null
                        ? caja.getEgresos()
                        : 0.0;

        caja.setEgresos(
                egresosActuales + valor
        );


        // ==================================================
        // 💰 ACTUALIZAR SALDO
        // ==================================================

        actualizarSaldo(
                caja
        );


        // ==================================================
        // 💾 GUARDAR
        // ==================================================

        return cajaRepository.save(
                caja
        );
    }


    // ==================================================
    // 🔒 CERRAR CAJA
    // ==================================================

    public Caja cerrarCaja(
            Integer negocioId
    ) {

        validarNegocio(negocioId);


        // ==================================================
        // 🔎 OBTENER CAJA ABIERTA DEL NEGOCIO
        // ==================================================

        Caja caja =
                obtenerCajaAbierta(
                        negocioId
                );

        if (caja == null) {

            throw new RuntimeException(
                    "No existe una caja abierta para este negocio."
            );
        }


        // ==================================================
        // 📅 FECHA DE CIERRE
        // ==================================================

        caja.setFechaCierre(
                LocalDateTime.now()
        );


        // ==================================================
        // 💰 CALCULAR SALDO FINAL
        // ==================================================

        actualizarSaldo(
                caja
        );


        // ==================================================
        // 📦 CAMBIAR ESTADO
        // ==================================================

        caja.setEstado(
                "CERRADA"
        );


        // ==================================================
        // 💾 GUARDAR
        // ==================================================

        return cajaRepository.save(
                caja
        );
    }


    // ==================================================
    // 💰 ACTUALIZAR SALDO
    // ==================================================

    private void actualizarSaldo(
            Caja caja
    ) {

        double saldoInicial =
                caja.getSaldoInicial() != null
                        ? caja.getSaldoInicial()
                        : 0.0;

        double ingresos =
                caja.getIngresos() != null
                        ? caja.getIngresos()
                        : 0.0;

        double egresos =
                caja.getEgresos() != null
                        ? caja.getEgresos()
                        : 0.0;

        caja.setSaldoFinal(
                saldoInicial
                        + ingresos
                        - egresos
        );
    }


    // ==================================================
    // 🔎 VALIDAR VALOR
    // ==================================================

    private void validarValor(
            Double valor
    ) {

        if (valor == null || valor <= 0) {

            throw new RuntimeException(
                    "El valor debe ser mayor que cero."
            );
        }
    }


    // ==================================================
    // 🔒 VALIDAR NEGOCIO
    // ==================================================

    private void validarNegocio(
            Integer negocioId
    ) {

        if (negocioId == null) {

            throw new IllegalStateException(
                    "No se pudo identificar el negocio del usuario."
            );
        }
    }
}