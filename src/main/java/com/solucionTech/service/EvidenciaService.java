package com.solucionTech.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.Optional;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.solucionTech.model.Atencion;
import com.solucionTech.model.EstadoAtencion;
import com.solucionTech.model.Evidencia;
import com.solucionTech.repository.EvidenciaRepository;
import com.solucionTech.repository.AtencionRepository;


@Service
public class EvidenciaService {

    private static final String CARPETA_EVIDENCIAS =
            "uploads/evidencias";

    private final EvidenciaRepository evidenciaRepository;
    private final AtencionRepository atencionRepository;
    
    public EvidenciaService(
            EvidenciaRepository evidenciaRepository,
            AtencionRepository atencionRepository) {

        this.evidenciaRepository = evidenciaRepository;
        this.atencionRepository = atencionRepository;
    }


    @Transactional
    public Evidencia guardar(
            MultipartFile archivo,
            String descripcion,
            Atencion atencion) throws IOException {

        validarArchivo(archivo);

        Path carpeta = Paths.get(CARPETA_EVIDENCIAS);
        Files.createDirectories(carpeta);

        String nombreOriginal = archivo.getOriginalFilename();
        String extension = obtenerExtension(nombreOriginal);
        String nombreGuardado = UUID.randomUUID() + extension;

        Path ruta = carpeta.resolve(nombreGuardado);

        Files.copy(
                archivo.getInputStream(),
                ruta,
                StandardCopyOption.REPLACE_EXISTING);

        Evidencia evidencia = new Evidencia();
        evidencia.setNombreArchivo(nombreOriginal);
        evidencia.setRutaArchivo(ruta.toString());
        evidencia.setTipoArchivo(archivo.getContentType());
        evidencia.setDescripcion(descripcion);
        evidencia.setVisibleParaCliente(true);
        evidencia.setAtencion(atencion);

        Evidencia evidenciaGuardada =
                evidenciaRepository.save(evidencia);

        if (atencion.getDiagnosticoInicial() != null
                && !atencion.getDiagnosticoInicial().isBlank()) {

            atencion.setEstado(
                    EstadoAtencion.ESPERANDO_APROBACION);

            atencion.setFechaUltimaActualizacion(
                    java.time.LocalDateTime.now());

            atencionRepository.save(atencion);
        }

        return evidenciaGuardada;

    }

    @Transactional(readOnly = true)
    public Optional<Evidencia> buscarPorId(Long id) {
        return evidenciaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Evidencia> listarPorAtencion(Atencion atencion) {
        return evidenciaRepository
                .findByAtencionOrderByFechaSubidaDesc(atencion);
    }

    @Transactional(readOnly = true)
    public List<Evidencia> listarVisiblesParaCliente(
            Atencion atencion) {

        return evidenciaRepository
                .findByAtencionAndVisibleParaClienteTrueOrderByFechaSubidaDesc(
                        atencion);
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una imagen.");
        }

        String tipo = archivo.getContentType();

        if (!"image/jpeg".equals(tipo)
                && !"image/png".equals(tipo)) {
            throw new IllegalArgumentException(
                    "Solo se permiten imágenes JPG, JPEG o PNG.");
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null
                || !nombreArchivo.contains(".")) {
            throw new IllegalArgumentException(
                    "El archivo no tiene una extensión válida.");
        }

        String extension = nombreArchivo.substring(
                nombreArchivo.lastIndexOf(".")).toLowerCase();

        if (!extension.equals(".jpg")
                && !extension.equals(".jpeg")
                && !extension.equals(".png")) {
            throw new IllegalArgumentException(
                    "Extensión no permitida.");
        }

        return extension;
    }

}
																																																				