package com.solucionTech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.Proveedor;

public interface ProveedorRepository
        extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByRuc(String ruc);

    boolean existsByRuc(String ruc);

    List<Proveedor> findByEstadoTrueOrderByRazonSocialAsc();
}
