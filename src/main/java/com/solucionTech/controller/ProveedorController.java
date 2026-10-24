package com.solucionTech.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.solucionTech.model.Proveedor;
import com.solucionTech.service.ProveedorService;
@Controller @RequestMapping("/proveedores")
public class ProveedorController {
 private final ProveedorService service; public ProveedorController(ProveedorService service){this.service=service;}
 @GetMapping public String listar(Model m){m.addAttribute("proveedores",service.listarTodos());return "proveedores/lista";}
 @GetMapping("/nuevo") public String nuevo(Model m){m.addAttribute("proveedor",new Proveedor());return "proveedores/formulario";}
 @GetMapping("/editar/{id}") public String editar(@PathVariable Long id,Model m){m.addAttribute("proveedor",service.buscarPorId(id).orElseThrow());return "proveedores/formulario";}
 @PostMapping("/guardar") public String guardar(@ModelAttribute Proveedor p,RedirectAttributes ra){try{if(p.getId()==null)service.registrar(p);else service.actualizar(p);ra.addFlashAttribute("mensaje","Proveedor guardado correctamente.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/proveedores";}
 @PostMapping("/eliminar/{id}") public String eliminar(@PathVariable Long id,RedirectAttributes ra){try{service.eliminarDefinitivamente(id);ra.addFlashAttribute("mensaje","Proveedor eliminado definitivamente.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/proveedores";}
 @PostMapping("/activar/{id}") public String activar(@PathVariable Long id,RedirectAttributes ra){try{service.activar(id);ra.addFlashAttribute("mensaje","Proveedor activado correctamente.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/proveedores";}
}
