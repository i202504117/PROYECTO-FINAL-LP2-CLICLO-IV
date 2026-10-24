package com.solucionTech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.solucionTech.model.Usuario;
import com.solucionTech.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
                "usuarios",
                usuarioService.listarTodos());

        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute(
                "roles",
                com.solucionTech.model.Rol.values());

        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("usuario") Usuario usuario,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            usuarioService.registrar(usuario);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Usuario registrado correctamente.");

            return "redirect:/usuarios";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute(
                    "roles",
                    com.solucionTech.model.Rol.values());

            return "usuarios/formulario";
        }
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.desactivar(id);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Usuario eliminado del listado activo; el historial se conserva.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/usuarios";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            usuarioService.activar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario activado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
