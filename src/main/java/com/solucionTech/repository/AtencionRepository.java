package com.solucionTech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.Atencion;
import com.solucionTech.model.EstadoAtencion;
import com.solucionTech.model.Usuario;

public interface AtencionRepository
        extends JpaRepository<Atencion, Long> {

    Optional<Atencion> findByCodigoSeguimiento(
            String codigoSeguimiento);

    boolean existsByCodigoSeguimiento(
            String codigoSeguimiento);

    boolean existsByClienteId(Long clienteId);

    List<Atencion> findByEstadoOrderByFechaRegistroDesc(
            EstadoAtencion estado);

    List<Atencion> findByTecnicoAsignadoOrderByFechaRegistroDesc(
            Usuario tecnico);
}
