package com.solucionTech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.solucionTech.model.Producto;
import com.solucionTech.service.CategoriaService;
import com.solucionTech.service.ProductoService;
import com.solucionTech.service.ProveedorService;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;

    public ProductoController(
            ProductoService productoService,
            CategoriaService categoriaService,
            ProveedorService proveedorService) {

        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute(
                "productos",
                productoService.listarTodos());

        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("producto", new Producto());
        cargarListas(model);

        return "productos/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto no encontrado.")));
        cargarListas(model);
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("producto") Producto producto,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam(value = "proveedorId",
                    required = false) Long proveedorId,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            producto.setCategoria(
                    categoriaService.buscarPorId(categoriaId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Categoría no encontrada.")));

            if (proveedorId != null) {
                producto.setProveedor(
                        proveedorService.buscarPorId(proveedorId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                        "Proveedor no encontrado.")));
            } else {
                producto.setProveedor(null);
            }

            if (producto.getId() == null) {
                productoService.registrar(producto);
            } else {
                productoService.actualizar(producto);
            }

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Producto registrado correctamente.");

            return "redirect:/productos";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarListas(model);
            return "productos/formulario";
        }
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            productoService.desactivar(id);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Producto eliminado del listado activo; el historial se conserva.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/productos";
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            productoService.activar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto activado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarDefinitivamente(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarDefinitivamente(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado definitivamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/productos";
    }

    private void cargarListas(Model model) {
        model.addAttribute(
                "categorias",
                categoriaService.listarActivas());

        model.addAttribute(
                "proveedores",
                proveedorService.listarActivos());
    }
}
