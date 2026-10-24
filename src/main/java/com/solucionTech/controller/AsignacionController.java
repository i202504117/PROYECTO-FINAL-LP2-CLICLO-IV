package com.solucionTech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.solucionTech.model.Atencion;
import com.solucionTech.model.Usuario;
import com.solucionTech.service.AtencionService;
import com.solucionTech.service.UsuarioService;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionController {

    private final AtencionService atencionService;
    private final UsuarioService usuarioService;

    public AsignacionController(
            AtencionService atencionService,
            UsuarioService usuarioService) {

        this.atencionService = atencionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/atencion/{id}")
    public String mostrarFormulario(
            @PathVariable Long id,
            Model model) {

        Atencion atencion = atencionService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atención no encontrada."));

        model.addAttribute("atencion", atencion);

        model.addAttribute(
                "tecnicos",
                usuarioService.listarTecnicosActivos());

        return "asignaciones/formulario";
    }

    @PostMapping("/atencion/{id}")
    public String asignar(
            @PathVariable Long id,
            @RequestParam Long tecnicoId,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario tecnico = usuarioService
                    .buscarPorId(tecnicoId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Técnico no encontrado."));

            atencionService.asignarTecnico(id, tecnico);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Responsable asignado correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/atenciones/detalle/" + id;
    }
}
