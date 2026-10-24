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

import com.solucionTech.model.Compra;
import com.solucionTech.model.DetalleCompra;
import com.solucionTech.model.Producto;
import com.solucionTech.model.Proveedor;
import com.solucionTech.model.Usuario;
import com.solucionTech.service.CompraService;
import com.solucionTech.service.ProductoService;
import com.solucionTech.service.ProveedorService;
import com.solucionTech.service.UsuarioService;

@Controller
@RequestMapping("/compras")
public class CompraController {

	private final CompraService compraService;
	private final ProductoService productoService;
	private final ProveedorService proveedorService;
	private final UsuarioService usuarioService;

	public CompraController(CompraService compraService, ProductoService productoService,
			ProveedorService proveedorService, UsuarioService usuarioService) {

		this.compraService = compraService;
		this.productoService = productoService;
		this.proveedorService = proveedorService;
		this.usuarioService = usuarioService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("compras", compraService.listarTodas());

		return "compras/lista";
	}

	@GetMapping("/nueva")
	public String mostrarFormulario(Model model) {
		model.addAttribute("proveedores", proveedorService.listarActivos());

		model.addAttribute("productos", productoService.listarActivos());

		return "compras/formulario";
	}

	@PostMapping("/guardar")
	public String guardar(@RequestParam(value = "proveedorId", required = false) Long proveedorId,
			@RequestParam(value = "productoId", required = false) Long productoId,
			@RequestParam(value = "cantidad", required = false) Integer cantidad,
			@RequestParam(value = "precioUnitario", required = false) BigDecimal precioUnitario,
			@AuthenticationPrincipal UserDetails usuarioActual, RedirectAttributes redirectAttributes) {

		try {
			if (proveedorId == null) {
				throw new IllegalArgumentException("Debe seleccionar un proveedor.");
			}
			if (productoId == null) {
				throw new IllegalArgumentException("Debe seleccionar un producto válido de la lista sugerida.");
			}
			if (cantidad == null || cantidad <= 0) {
				throw new IllegalArgumentException("La cantidad debe ser al menos 1.");
			}
			if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException("Debe ingresar un precio unitario válido.");
			}

			Usuario usuario = usuarioService.buscarPorUsername(usuarioActual.getUsername())
					.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

			Proveedor proveedor = proveedorService.buscarPorId(proveedorId)
					.orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado."));

			Producto producto = productoService.buscarPorId(productoId)
					.orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

			Compra compra = new Compra();
			compra.setProveedor(proveedor);
			compra.setUsuario(usuario);

			DetalleCompra detalle = new DetalleCompra();
			detalle.setProducto(producto);
			detalle.setCantidad(cantidad);
			detalle.setPrecioUnitario(precioUnitario);

			compra.agregarDetalle(detalle);

			Compra compraGuardada = compraService.registrar(compra);

			redirectAttributes.addFlashAttribute("mensaje", "Compra registrada correctamente.");

			return "redirect:/compras/" + compraGuardada.getId();

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());

			return "redirect:/compras/nueva";
		}
	}

	@GetMapping("/{id}")
	public String detalle(@PathVariable Long id, Model model) {

		model.addAttribute("compra", compraService.buscarPorId(id));

		return "compras/detalle";
	}

	@PostMapping("/{id}/confirmar")
	public String confirmar(@PathVariable Long id, @AuthenticationPrincipal UserDetails usuarioActual,
			RedirectAttributes redirectAttributes) {

		try {
			Usuario usuario = usuarioService.buscarPorUsername(usuarioActual.getUsername())
					.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

			compraService.confirmar(id, usuario);

			redirectAttributes.addFlashAttribute("mensaje", "Compra confirmada y stock actualizado.");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/compras/" + id;
	}
}