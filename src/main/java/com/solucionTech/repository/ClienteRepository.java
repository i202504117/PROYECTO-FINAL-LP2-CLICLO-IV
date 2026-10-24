package com.solucionTech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByTelefono(String telefono);

    Optional<Cliente> findByEmail(String email);

    boolean existsByTelefono(String telefono);
    boolean existsByTelefonoAndIdNot(String telefono, Long id);
}
