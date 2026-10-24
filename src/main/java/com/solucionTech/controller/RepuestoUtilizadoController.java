package com.solucionTech.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.solucionTech.service.ProductoService;
import com.solucionTech.service.RepuestoUtilizadoService;
import com.solucionTech.service.UsuarioService;

@Controller
@RequestMapping("/atenciones")
public class RepuestoUtilizadoController {

    private final AtencionService atencionService;
    private final ProductoService productoService;
    private final RepuestoUtilizadoService repuestoService;
    private final UsuarioService usuarioService;

    public RepuestoUtilizadoController(
            AtencionService atencionService,
            ProductoService productoService,
            RepuestoUtilizadoService repuestoService,
            UsuarioService usuarioService) {

        this.atencionService = atencionService;
        this.productoService = productoService;
        this.repuestoService = repuestoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{atencionId}/repuestos/nuevo")
    public String mostrarFormulario(
            @PathVariable Long atencionId,
            Model model) {

        Atencion atencion = atencionService
                .buscarPorId(atencionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atención no encontrada."));

        model.addAttribute("atencion", atencion);

        model.addAttribute(
                "repuestos",
                productoService.listarPorTipo(
                        com.solucionTech.model.TipoProducto.REPUESTO));

        return "repuestos/formulario";
    }

    @PostMapping("/{atencionId}/repuestos/guardar")
    public String guardar(
            @PathVariable Long atencionId,
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            @AuthenticationPrincipal UserDetails usuarioActual,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioService
                    .buscarPorUsername(usuarioActual.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Usuario no encontrado."));

            repuestoService.registrar(
                    atencionId,
                    productoId,
                    cantidad,
                    usuario);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Repuesto registrado y stock actualizado.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/atenciones/detalle/" + atencionId;
    }
}
