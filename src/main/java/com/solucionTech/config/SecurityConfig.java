package com.solucionTech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserDetailsService usuarioDetailsService)
            throws Exception {

        http
            .userDetailsService(usuarioDetailsService)

            .authorizeHttpRequests(auth -> auth

                // Recursos públicos
                .requestMatchers(
                        "/css/**",
                        "/js/**",
                        "/imagenes/**",
                        "/login",
                        "/logout",
                        "/consulta-atencion",
                        "/consulta-atencion/**",
                        "/consulta/**"
                ).permitAll()

                // Usuarios: únicamente administrador
                .requestMatchers("/usuarios/**")
                .hasRole("ADMINISTRADOR")

                // Catálogos maestros: únicamente administrador
                .requestMatchers("/categorias/**", "/proveedores/**")
                .hasRole("ADMINISTRADOR")

                // Reportes: únicamente administrador
                .requestMatchers("/reportes/**")
                .hasRole("ADMINISTRADOR")

                // Asignación de técnicos: únicamente administrador
                .requestMatchers("/asignaciones/**")
                .hasRole("ADMINISTRADOR")

                // Compras e inventario administrativo
                .requestMatchers("/compras/**")
                .hasRole("ADMINISTRADOR")

                // El técnico solo puede consultar la lista del catálogo (/productos),
                // pero no entrar a /productos/nuevo, ni editar, guardar o desactivar.
                .requestMatchers(HttpMethod.GET, "/productos")
                .hasAnyRole("ADMINISTRADOR", "TECNICO")

                // Administración de productos (nuevo, guardar, editar, activar, desactivar, eliminar)
                .requestMatchers("/productos/**")
                .hasRole("ADMINISTRADOR")

                // Clientes: administrador y técnico
                .requestMatchers("/clientes/eliminar/**")
                .hasRole("ADMINISTRADOR")
                .requestMatchers("/clientes/**")
                .hasAnyRole("ADMINISTRADOR", "TECNICO")

                // Atenciones: administrador y técnico
                .requestMatchers("/atenciones/eliminar/**")
                .hasRole("ADMINISTRADOR")
                .requestMatchers("/atenciones/**")
                .hasAnyRole("ADMINISTRADOR", "TECNICO")

                // Ventas: ambos roles
                .requestMatchers("/ventas/**")
                .hasAnyRole("ADMINISTRADOR", "TECNICO")

                // Página principal para usuarios autenticados
                .requestMatchers(HttpMethod.GET, "/")
                .hasAnyRole("ADMINISTRADOR", "TECNICO")

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(request ->
                    "/logout".equals(request.getRequestURI())
                    && (
                        "GET".equals(request.getMethod())
                        || "POST".equals(request.getMethod())
                    )
                )
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .headers(headers -> headers
                .cacheControl(Customizer.withDefaults())
            );

        return http.build();
    }
}