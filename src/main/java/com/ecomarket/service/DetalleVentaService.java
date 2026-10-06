package com.ecomarket.service;

import com.ecomarket.model.DetalleVenta;

import java.util.List;

public interface DetalleVentaService {

    List<DetalleVenta> listar();

    List<DetalleVenta> listarPorVenta(
            Integer idVenta
    );
}