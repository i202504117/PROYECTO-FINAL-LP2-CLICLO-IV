package com.solucionTech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.Atencion;
import com.solucionTech.model.RepuestoUtilizado;

public interface RepuestoUtilizadoRepository
        extends JpaRepository<RepuestoUtilizado, Long> {
    boolean existsByProductoId(Long productoId);

    List<RepuestoUtilizado>
    findByAtencionOrderByFechaDesc(Atencion atencion);
}
