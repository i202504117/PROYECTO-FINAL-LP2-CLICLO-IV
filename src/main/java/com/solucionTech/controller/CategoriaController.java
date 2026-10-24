package com.solucionTech.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.solucionTech.model.Categoria;
import com.solucionTech.service.CategoriaService;
@Controller @RequestMapping("/categorias")
public class CategoriaController {
 private final CategoriaService service; public CategoriaController(CategoriaService service){this.service=service;}
 @GetMapping public String listar(Model m){m.addAttribute("categorias",service.listarTodos());return "categorias/lista";}
 @GetMapping("/nuevo") public String nuevo(Model m){m.addAttribute("categoria",new Categoria());return "categorias/formulario";}
 @GetMapping("/editar/{id}") public String editar(@PathVariable Long id,Model m){m.addAttribute("categoria",service.buscarPorId(id).orElseThrow());return "categorias/formulario";}
 @PostMapping("/guardar") public String guardar(@ModelAttribute Categoria c,RedirectAttributes ra){try{if(c.getId()==null)service.registrar(c);else service.actualizar(c);ra.addFlashAttribute("mensaje","Categoría guardada correctamente.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/categorias";}
 @PostMapping("/eliminar/{id}") public String eliminar(@PathVariable Long id,RedirectAttributes ra){try{service.eliminarDefinitivamente(id);ra.addFlashAttribute("mensaje","Categoría eliminada definitivamente.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/categorias";}
 @PostMapping("/activar/{id}") public String activar(@PathVariable Long id,RedirectAttributes ra){try{service.activar(id);ra.addFlashAttribute("mensaje","Categoría activada correctamente.");}catch(Exception e){ra.addFlashAttribute("error",e.getMessage());}return "redirect:/categorias";}
}
