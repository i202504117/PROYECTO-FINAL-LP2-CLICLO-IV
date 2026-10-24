package com.solucionTech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.solucionTech.model.EstadoCompra;

import com.solucionTech.model.Categoria;
import com.solucionTech.model.Compra;

public interface CategoriaRepository
        extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    List<Categoria> findByEstadoTrueOrderByNombreAsc();
    List<Compra> findByEstado(EstadoCompra estado);

}
