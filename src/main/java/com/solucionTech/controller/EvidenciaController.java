package com.solucionTech.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.solucionTech.model.Atencion;
import com.solucionTech.model.Evidencia;
import com.solucionTech.service.AtencionService;
import com.solucionTech.service.EvidenciaService;
import com.solucionTech.service.UsuarioService;
import com.solucionTech.model.Usuario;

@Controller
public class EvidenciaController {

    private final EvidenciaService evidenciaService;
    private final AtencionService atencionService;
    private final UsuarioService usuarioService;

    public EvidenciaController(
            EvidenciaService evidenciaService,
            AtencionService atencionService,
            UsuarioService usuarioService) {
        this.evidenciaService = evidenciaService;
        this.atencionService = atencionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/atenciones/{id}/evidencias/nueva")
    public String mostrarFormulario(
            @PathVariable Long id,
            Model model) {

        Atencion atencion = atencionService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atención no encontrada"));

        model.addAttribute("atencion", atencion);

        return "evidencias/formulario";
    }

    @PostMapping("/atenciones/{id}/evidencias/guardar")
    public String guardar(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "final", defaultValue = "false") boolean evidenciaFinal,
            @AuthenticationPrincipal UserDetails usuarioActual,
            RedirectAttributes redirectAttributes) {

        try {
            Atencion atencion = atencionService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Atención no encontrada"));

            evidenciaService.guardar(archivo, descripcion, atencion);

            if (evidenciaFinal) {
                Usuario usuario = usuarioService.buscarPorUsername(usuarioActual.getUsername())
                        .orElseThrow(() -> new IllegalArgumentException("Usuario actual no encontrado."));
                atencionService.finalizarPorEvidencia(id, usuario);
            }

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    evidenciaFinal
                            ? "Evidencia final guardada. Atención finalizada y equipo listo para recojo."
                            : "Evidencia guardada correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se pudo guardar la imagen: " + e.getMessage());
        }

        return "redirect:/atenciones/detalle/" + id;
    }

    @GetMapping("/consulta-atencion/evidencia/{id}")
    public ResponseEntity<ByteArrayResource> visualizar(
            @PathVariable Long id,
            @RequestParam("codigo") String codigo) throws IOException {

        Evidencia evidencia = evidenciaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Evidencia no encontrada"));

        boolean codigoCorrecto = evidencia.getAtencion()
                .getCodigoSeguimiento()
                .equalsIgnoreCase(codigo);

        if (!codigoCorrecto
                || !Boolean.TRUE.equals(
                        evidencia.getVisibleParaCliente())) {
            return ResponseEntity.notFound().build();
        }

        Path ruta = Paths.get(evidencia.getRutaArchivo());

        if (!Files.exists(ruta)) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource recurso =
                new ByteArrayResource(Files.readAllBytes(ruta));

        MediaType tipo = MediaType.parseMediaType(
                evidencia.getTipoArchivo());

        return ResponseEntity.ok()
                .contentType(tipo)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + evidencia.getNombreArchivo()
                                + "\"")
                .body(recurso);
    }
}
