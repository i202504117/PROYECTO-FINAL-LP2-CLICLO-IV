package com.solucionTech.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.solucionTech.model.Rol;
import com.solucionTech.model.Usuario;
import com.solucionTech.model.Categoria;
import com.solucionTech.repository.CategoriaRepository;
import com.solucionTech.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner crearAdministradorInicial(
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            String username = "admin";

            if (!usuarioRepository.existsByUsername(username)) {
                Usuario administrador = new Usuario();

                administrador.setNombres("Administrador");
                administrador.setApellidos("Principal");
                administrador.setUsername(username);
                administrador.setPassword(
                        passwordEncoder.encode("Admin123*"));
                administrador.setRol(Rol.ADMINISTRADOR);
                administrador.setEspecialidad("Gestión general");
                administrador.setTelefono("999999999");
                administrador.setEmail("admin@soluciontech.com");
                administrador.setEstado(true);

                usuarioRepository.save(administrador);

                System.out.println(
                        "Usuario administrador inicial creado correctamente.");
            }

            crearCategoriaSiNoExiste(categoriaRepository, "Laptops",
                    "Equipos portátiles y accesorios relacionados.");
            crearCategoriaSiNoExiste(categoriaRepository, "Celulares",
                    "Teléfonos móviles y accesorios.");
            crearCategoriaSiNoExiste(categoriaRepository, "Cargadores",
                    "Cargadores, adaptadores y fuentes de poder.");
            crearCategoriaSiNoExiste(categoriaRepository, "Baterías",
                    "Baterías y repuestos de alimentación.");
            crearCategoriaSiNoExiste(categoriaRepository, "Almacenamiento",
                    "Discos, memorias USB, SSD y tarjetas de memoria.");
            crearCategoriaSiNoExiste(categoriaRepository, "Pantallas",
                    "Pantallas, displays y repuestos de imagen.");
            crearCategoriaSiNoExiste(categoriaRepository, "Periféricos",
                    "Teclados, mouse, audífonos y otros periféricos.");
            crearCategoriaSiNoExiste(categoriaRepository, "Mantenimiento",
                    "Productos para limpieza y mantenimiento de equipos.");
        };
    }

    private void crearCategoriaSiNoExiste(CategoriaRepository repository,
            String nombre, String descripcion) {
        if (!repository.existsByNombreIgnoreCase(nombre)) {
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoria.setDescripcion(descripcion);
            categoria.setEstado(true);
            repository.save(categoria);
        }
    }
}
