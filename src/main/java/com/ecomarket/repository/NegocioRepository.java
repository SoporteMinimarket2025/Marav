package com.ecomarket.repository;

import com.ecomarket.model.Negocio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NegocioRepository
        extends JpaRepository<Negocio, Integer> {
}