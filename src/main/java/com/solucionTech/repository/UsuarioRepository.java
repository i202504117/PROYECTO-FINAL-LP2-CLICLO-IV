package com.solucionTech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solucionTech.model.Rol;
import com.solucionTech.model.Usuario;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Usuario> findByRolAndEstadoTrueOrderByApellidosAsc(
            Rol rol);
}
