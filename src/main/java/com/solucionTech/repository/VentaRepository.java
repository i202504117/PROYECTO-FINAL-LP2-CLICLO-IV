package com.solucionTech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.EstadoVenta;
import com.solucionTech.model.Venta;

public interface VentaRepository
        extends JpaRepository<Venta, Long> {

    Optional<Venta> findByNumeroVenta(String numeroVenta);

    boolean existsByNumeroVenta(String numeroVenta);

    boolean existsByClienteId(Long clienteId);

    List<Venta> findByEstado(EstadoVenta estado);
    List<Venta> findByEstadoAndFechaVentaBetween(EstadoVenta estado,
            java.time.LocalDateTime desde, java.time.LocalDateTime hasta);
}
