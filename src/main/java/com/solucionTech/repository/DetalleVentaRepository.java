package com.solucionTech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.DetalleVenta;

public interface DetalleVentaRepository
        extends JpaRepository<DetalleVenta, Long> {
    boolean existsByProductoId(Long productoId);
}
