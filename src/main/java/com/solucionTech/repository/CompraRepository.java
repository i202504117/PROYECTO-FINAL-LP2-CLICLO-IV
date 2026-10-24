package com.solucionTech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.Compra;
import com.solucionTech.model.EstadoCompra;

public interface CompraRepository
        extends JpaRepository<Compra, Long> {

    Optional<Compra> findByNumeroCompra(String numeroCompra);

    boolean existsByNumeroCompra(String numeroCompra);

    boolean existsByProveedorId(Long proveedorId);

    List<Compra> findByEstado(EstadoCompra estado);
    List<Compra> findByEstadoAndFechaCompraBetween(EstadoCompra estado,
            java.time.LocalDateTime desde, java.time.LocalDateTime hasta);
}
