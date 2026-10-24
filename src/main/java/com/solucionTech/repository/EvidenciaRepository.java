package com.solucionTech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.Atencion;
import com.solucionTech.model.Evidencia;

public interface EvidenciaRepository
        extends JpaRepository<Evidencia, Long> {

    List<Evidencia> findByAtencionOrderByFechaSubidaDesc(
            Atencion atencion);

    List<Evidencia> findByAtencionAndVisibleParaClienteTrueOrderByFechaSubidaDesc(
            Atencion atencion);
}
