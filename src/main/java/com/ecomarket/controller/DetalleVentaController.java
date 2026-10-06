package com.ecomarket.controller;

import com.ecomarket.model.DetalleVenta;
import com.ecomarket.repository.DetalleVentaRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detalle-venta")
@CrossOrigin
public class DetalleVentaController {

    private final DetalleVentaRepository repository;

    public DetalleVentaController(
            DetalleVentaRepository repository
    ) {
        this.repository = repository;
    }

    @GetMapping("/venta/{idVenta}")
    public List<DetalleVenta> porVenta(
            @PathVariable Integer idVenta
    ) {

        return repository.findByIdVenta(
                idVenta
        );
    }
}