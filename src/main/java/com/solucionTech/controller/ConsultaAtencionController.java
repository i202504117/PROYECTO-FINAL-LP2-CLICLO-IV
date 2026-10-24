package com.solucionTech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import com.solucionTech.model.Atencion;
import com.solucionTech.service.AtencionService;
import com.solucionTech.service.EvidenciaService;

@Controller
public class ConsultaAtencionController {
    private final AtencionService atencionService;
    private final EvidenciaService evidenciaService;

    public ConsultaAtencionController(AtencionService atencionService,
            EvidenciaService evidenciaService) {
        this.atencionService = atencionService;
        this.evidenciaService = evidenciaService;
    }

    @GetMapping("/consulta-atencion")
    public String mostrarConsulta(@RequestParam(required = false) String codigo,
            Model model) {
        if (codigo != null && !codigo.isBlank()) {
            Atencion atencion = atencionService.buscarPorCodigo(codigo).orElse(null);
            if (atencion == null) {
                model.addAttribute("error", "No se encontró una atención con ese código.");
            } else {
                model.addAttribute("atencion", atencion);
                model.addAttribute("evidencias", evidenciaService.listarVisiblesParaCliente(atencion));
            }
        }
        return "consulta-atencion";
    }

    @GetMapping("/consulta/{codigo}")
    public String consultaDirecta(@PathVariable String codigo) {
        return "redirect:/consulta-atencion?codigo=" + codigo;
    }
}
