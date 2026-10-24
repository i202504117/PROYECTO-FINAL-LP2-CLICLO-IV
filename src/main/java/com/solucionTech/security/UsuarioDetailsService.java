package com.solucionTech.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.solucionTech.model.Usuario;
import com.solucionTech.repository.UsuarioRepository;

@Service("usuarioDetailsService")
public class UsuarioDetailsService
        implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(
            UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado."));

        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new UsernameNotFoundException(
                    "El usuario está inactivo.");
        }

        if (usuario.getRol() == null) {
            throw new UsernameNotFoundException(
                    "El usuario no tiene un rol asignado.");
        }

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name())
                .disabled(!Boolean.TRUE.equals(
                        usuario.getEstado()))
                .build();
    }
}
