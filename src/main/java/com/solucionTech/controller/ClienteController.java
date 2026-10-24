package com.solucionTech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.solucionTech.model.Cliente;
import com.solucionTech.service.ClienteService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado.")));
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("cliente") Cliente cliente,
            RedirectAttributes redirectAttributes) {

        try {
            if (cliente.getId() == null) {
                clienteService.registrar(cliente);
            } else {
                clienteService.actualizar(cliente);
            }
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Cliente registrado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/clientes";
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            clienteService.desactivar(id);
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Cliente eliminado del listado activo; el historial se conserva.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/clientes";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            clienteService.activar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente activado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarDefinitivamente(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            clienteService.eliminarDefinitivamente(id);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente eliminado definitivamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }
}
