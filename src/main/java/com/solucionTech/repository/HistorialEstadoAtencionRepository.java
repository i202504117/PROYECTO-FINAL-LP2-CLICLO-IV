package com.solucionTech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.Atencion;
import com.solucionTech.model.HistorialEstadoAtencion;

public interface HistorialEstadoAtencionRepository
        extends JpaRepository<HistorialEstadoAtencion, Long> {

    List<HistorialEstadoAtencion>
    findByAtencionOrderByFechaAsc(Atencion atencion);
}
