package com.solucionTech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.DetalleCompra;

public interface DetalleCompraRepository

extends JpaRepository<DetalleCompra, Long> {

boolean existsByProductoId(Long productoId);
}

