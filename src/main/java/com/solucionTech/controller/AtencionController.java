package com.solucionTech.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.solucionTech.model.Atencion;
import com.solucionTech.model.Cliente;
import com.solucionTech.model.Usuario;
import com.solucionTech.service.AtencionService;
import com.solucionTech.service.ClienteService;
import com.solucionTech.service.RepuestoUtilizadoService;
import com.solucionTech.service.UsuarioService;

@Controller
@RequestMapping("/atenciones")
public class AtencionController {
    private final AtencionService atencionService;
    private final ClienteService clienteService;
    private final RepuestoUtilizadoService repuestoService;
    private final UsuarioService usuarioService;

    public AtencionController(AtencionService atencionService,
            ClienteService clienteService,
            RepuestoUtilizadoService repuestoService,
            UsuarioService usuarioService) {
        this.atencionService = atencionService;
        this.clienteService = clienteService;
        this.repuestoService = repuestoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("atenciones", atencionService.listarTodas());
        return "atenciones/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("atencion", new Atencion());
        model.addAttribute("clientes", clienteService.listarTodos());
        return "atenciones/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("atencion") Atencion atencion,
            @RequestParam(value = "clienteId", required = false) Long clienteId,
            @RequestParam(value = "nuevoNombres", required = false) String nuevoNombres,
            @RequestParam(value = "nuevoApellidos", required = false) String nuevoApellidos,
            @RequestParam(value = "nuevoTelefono", required = false) String nuevoTelefono,
            @RequestParam(value = "nuevoEmail", required = false) String nuevoEmail,
            @RequestParam(value = "nuevoDireccion", required = false) String nuevoDireccion,
            @AuthenticationPrincipal UserDetails usuarioActual,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorUsername(usuarioActual.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario actual no encontrado."));
            Cliente cliente;
            if (clienteId != null) {
                cliente = clienteService.buscarPorId(clienteId)
                        .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
            } else {
                if (esVacio(nuevoNombres) || esVacio(nuevoApellidos) || esVacio(nuevoTelefono)) {
                    throw new IllegalArgumentException("Para crear un cliente debe indicar nombres, apellidos y teléfono.");
                }
                cliente = new Cliente();
                cliente.setNombres(nuevoNombres.trim());
                cliente.setApellidos(nuevoApellidos.trim());
                cliente.setTelefono(nuevoTelefono.trim());
                cliente.setEmail(limpiar(nuevoEmail));
                cliente.setDireccion(limpiar(nuevoDireccion));
                cliente = clienteService.registrar(cliente);
            }
            atencion.setCliente(cliente);
            atencionService.registrar(atencion, usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Atención registrada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo registrar la atención: " + e.getMessage());
        }
        return "redirect:/atenciones";
    }

    @PostMapping("/{id}/aceptar")
    public String aceptar(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails usuarioActual,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario tecnico = usuarioService.buscarPorUsername(usuarioActual.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario actual no encontrado."));
            atencionService.aceptarPorTecnico(id, tecnico);
            redirectAttributes.addFlashAttribute("mensaje", "Atención aceptada. El estado cambió a equipo en reparación.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/atenciones/detalle/" + id;
    }

    @PostMapping("/{id}/repuesto-disponible")
    public String repuestoDisponible(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails usuarioActual,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.buscarPorUsername(usuarioActual.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario actual no encontrado."));
            atencionService.marcarRepuestoDisponible(id, usuario);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Repuesto disponible. La atención pasó a equipo en reparación.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/atenciones/detalle/" + id;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            atencionService.eliminarTotal(id);
            redirectAttributes.addFlashAttribute("mensaje", "Atención eliminada completamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la atención: " + e.getMessage());
        }
        return "redirect:/atenciones";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Atencion atencion = atencionService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Atención no encontrada"));
        model.addAttribute("atencion", atencion);
        model.addAttribute("repuestosUtilizados", repuestoService.listarPorAtencion(id));
        return "atenciones/detalle";
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String limpiar(String valor) {
        return esVacio(valor) ? null : valor.trim();
    }
}
