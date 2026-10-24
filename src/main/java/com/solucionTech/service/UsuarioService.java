package com.solucionTech.service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Comparator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solucionTech.model.Usuario;
import com.solucionTech.repository.UsuarioRepository;
import com.solucionTech.model.Rol;


@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    @Transactional(readOnly = true)
    public List<Usuario> listarTecnicosActivos() {
        List<Usuario> responsables = new ArrayList<>(usuarioRepository
                .findByRolAndEstadoTrueOrderByApellidosAsc(Rol.TECNICO));
        responsables.addAll(usuarioRepository
                .findByRolAndEstadoTrueOrderByApellidosAsc(Rol.ADMINISTRADOR));
        responsables.sort(Comparator.comparing(Usuario::getApellidos,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        return responsables;
    }


    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public Usuario registrar(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario es obligatorio.");
        }

        if (usuario.getUsername() == null
                || usuario.getUsername().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio.");
        }

        if (usuario.getPassword() == null
                || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        if (usuario.getRol() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un rol.");
        }

        if (usuarioRepository.existsByUsername(
                usuario.getUsername())) {
            throw new IllegalArgumentException(
                    "El nombre de usuario ya existe.");
        }

        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword()));

        usuario.setEstado(true);

        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario actualizar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void desactivar(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado."));

        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void activar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado."));
        usuario.setEstado(true);
        usuarioRepository.save(usuario);
    }

    
    
}
