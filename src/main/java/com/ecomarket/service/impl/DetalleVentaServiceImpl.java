package com.ecomarket.service.impl;

import com.ecomarket.model.DetalleVenta;
import com.ecomarket.repository.DetalleVentaRepository;
import com.ecomarket.service.DetalleVentaService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleVentaServiceImpl
        implements DetalleVentaService {

    private final DetalleVentaRepository repository;

    public DetalleVentaServiceImpl(
            DetalleVentaRepository repository) {

        this.repository = repository;
    }

    @Override
    public List<DetalleVenta> listar() {

        return repository.findAll();
    }

    @Override
    public List<DetalleVenta> listarPorVenta(
            Integer idVenta) {

        return repository.findByIdVenta(idVenta);
    }
}