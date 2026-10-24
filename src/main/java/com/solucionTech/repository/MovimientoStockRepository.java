package com.solucionTech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.MovimientoStock;
import com.solucionTech.model.Producto;

public interface MovimientoStockRepository
        extends JpaRepository<MovimientoStock, Long> {
    boolean existsByProductoId(Long productoId);

    List<MovimientoStock> findByProductoOrderByFechaDesc(
            Producto producto);
}
