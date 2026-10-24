package com.solucionTech.controller;

import java.math.BigDecimal;

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

import com.solucionTech.model.Cliente;
import com.solucionTech.model.DetalleVenta;
import com.solucionTech.model.Producto;
import com.solucionTech.model.Usuario;
import com.solucionTech.model.Venta;
import com.solucionTech.service.ClienteService;
import com.solucionTech.service.ProductoService;
import com.solucionTech.service.UsuarioService;
import com.solucionTech.service.VentaService;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    public VentaController(
            VentaService ventaService,
            ProductoService productoService,
            ClienteService clienteService,
            UsuarioService usuarioService) {

        this.ventaService = ventaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
                "ventas",
                ventaService.listarTodas());

        return "ventas/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model) {
        model.addAttribute(
                "productos",
                productoService.listarActivos());

        model.addAttribute(
                "clientes",
                clienteService.listarTodos());

        return "ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam Long productoId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam Integer cantidad,
            @RequestParam BigDecimal precioUnitario,
            @RequestParam String metodoPago,
            @AuthenticationPrincipal UserDetails usuarioActual,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioService
                    .buscarPorUsername(usuarioActual.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Usuario no encontrado."));

            Producto producto = productoService
                    .buscarPorId(productoId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado."));

            Venta venta = new Venta();
            venta.setUsuario(usuario);
            venta.setMetodoPago(metodoPago);

            if (clienteId != null) {
                Cliente cliente = clienteService
                        .buscarPorId(clienteId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Cliente no encontrado."));

                venta.setCliente(cliente);
            }

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);

            venta.agregarDetalle(detalle);

            Venta ventaGuardada = ventaService.registrar(venta);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Venta registrada correctamente.");

            return "redirect:/ventas/" + ventaGuardada.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());

            return "redirect:/ventas/nueva";
        }
    }

    @GetMapping("/{id}")
    public String detalle(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "venta",
                ventaService.buscarPorId(id));

        return "ventas/detalle";
    }

    @PostMapping("/{id}/confirmar")
    public String confirmar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails usuarioActual,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioService
                    .buscarPorUsername(usuarioActual.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Usuario no encontrado."));

            ventaService.confirmar(id, usuario);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Venta confirmada y stock descontado.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/ventas/" + id;
    }
}

