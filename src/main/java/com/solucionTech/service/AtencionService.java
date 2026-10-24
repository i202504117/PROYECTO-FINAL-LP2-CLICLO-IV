package com.solucionTech.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solucionTech.model.Atencion;
import com.solucionTech.model.EstadoAtencion;
import com.solucionTech.model.Usuario;
import com.solucionTech.repository.AtencionRepository;
import com.solucionTech.model.HistorialEstadoAtencion;
import com.solucionTech.model.Rol;
import com.solucionTech.repository.HistorialEstadoAtencionRepository;
import com.solucionTech.repository.EvidenciaRepository;


@Service
public class AtencionService {

    private final AtencionRepository atencionRepository;
    
    private final HistorialEstadoAtencionRepository historialRepository;
    private final EvidenciaRepository evidenciaRepository;


    public AtencionService(
            AtencionRepository atencionRepository,
            HistorialEstadoAtencionRepository historialRepository,
            EvidenciaRepository evidenciaRepository) {

        this.atencionRepository = atencionRepository;
        this.historialRepository = historialRepository;
        this.evidenciaRepository = evidenciaRepository;
    }


    @Transactional(readOnly = true)
    public List<Atencion> listarTodas() {
        return atencionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Atencion> buscarPorId(Long id) {
        return atencionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Atencion> buscarPorCodigo(
            String codigoSeguimiento) {

        return atencionRepository.findByCodigoSeguimiento(
                codigoSeguimiento.trim().toUpperCase());
    }

    @Transactional(readOnly = true)
    public List<Atencion> listarPorEstado(
            EstadoAtencion estado) {

        return atencionRepository
                .findByEstadoOrderByFechaRegistroDesc(estado);
    }

    @Transactional(readOnly = true)
    public List<Atencion> listarPorTecnico(Usuario tecnico) {
        return atencionRepository
                .findByTecnicoAsignadoOrderByFechaRegistroDesc(tecnico);
    }

    @Transactional
    public Atencion registrar(Atencion atencion) {
        return registrar(atencion, null);
    }

    @Transactional
    public Atencion registrar(Atencion atencion, Usuario usuarioActual) {
        if (atencion.getEstado() == null) {
            if (Boolean.TRUE.equals(atencion.getRequiereRepuesto())) {
                atencion.setEstado(EstadoAtencion.ESPERANDO_REPUESTO);
                if (usuarioActual != null
                        && usuarioActual.getRol() == Rol.TECNICO
                        && Boolean.TRUE.equals(usuarioActual.getEstado())) {
                    atencion.setTecnicoAsignado(usuarioActual);
                }
            } else if (usuarioActual != null
                    && usuarioActual.getRol() == Rol.TECNICO
                    && Boolean.TRUE.equals(usuarioActual.getEstado())) {
                atencion.setTecnicoAsignado(usuarioActual);
                atencion.setEstado(EstadoAtencion.ASIGNADA);
            } else {
                atencion.setEstado(EstadoAtencion.EN_ESPERA);
            }
        }

        Atencion atencionGuardada =
                atencionRepository.save(atencion);

        registrarHistorial(
                atencionGuardada,
                null,
                atencionGuardada.getEstado(),
                atencionGuardada.getTecnicoAsignado() != null
                        ? "Atención registrada y asignada automáticamente."
                        : "Atención registrada.",
                usuarioActual != null
                        ? usuarioActual.getUsername()
                        : "SISTEMA");

        return atencionGuardada;
    }

    @Transactional
    public void eliminarTotal(Long id) {
        Atencion atencion = atencionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atención no encontrada."));

        evidenciaRepository.findByAtencionOrderByFechaSubidaDesc(atencion)
                .forEach(evidencia -> {
                    try {
                        Path ruta = Paths.get(evidencia.getRutaArchivo());
                        Files.deleteIfExists(ruta);
                    } catch (Exception ignored) {
                        // El registro se elimina aunque el archivo ya no exista.
                    }
                });

        // Atencion usa cascade + orphanRemoval para sus hijos.
        atencionRepository.delete(atencion);
    }


    @Transactional
    public Atencion cambiarEstado(
            Long id,
            EstadoAtencion nuevoEstado) {

        Atencion atencion = atencionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atención no encontrada"));

        EstadoAtencion estadoAnterior =
                atencion.getEstado();

        atencion.setEstado(nuevoEstado);
        atencion.setFechaUltimaActualizacion(
                LocalDateTime.now());

        Atencion atencionActualizada =
                atencionRepository.save(atencion);

        registrarHistorial(
                atencionActualizada,
                estadoAnterior,
                nuevoEstado,
                "Estado actualizado.",
                "SISTEMA");

        return atencionActualizada;
    }

    @Transactional
    public Atencion asignarTecnico(
            Long atencionId,
            Usuario tecnico) {

        if (tecnico == null) {
            throw new IllegalArgumentException(
                    "El técnico es obligatorio.");
        }

        if (tecnico.getRol() != Rol.TECNICO
                && tecnico.getRol() != Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException(
                    "El usuario seleccionado no puede ser responsable técnico.");
        }

        if (!Boolean.TRUE.equals(tecnico.getEstado())) {
            throw new IllegalArgumentException(
                    "El técnico seleccionado está inactivo.");
        }

        Atencion atencion = atencionRepository.findById(atencionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atención no encontrada."));

        if (atencion.getEstado() != EstadoAtencion.EN_ESPERA
                && atencion.getEstado() != EstadoAtencion.ASIGNADA) {

            throw new IllegalStateException(
                    "La atención no se puede asignar en su estado actual.");
        }

        atencion.setTecnicoAsignado(tecnico);
        atencion.setEstado(EstadoAtencion.ASIGNADA);
        atencion.setFechaUltimaActualizacion(
                java.time.LocalDateTime.now());

        return atencionRepository.save(atencion);
    }

    @Transactional
    public Atencion aceptarPorTecnico(Long atencionId, Usuario tecnico) {
        if (tecnico == null || (tecnico.getRol() != Rol.TECNICO
                && tecnico.getRol() != Rol.ADMINISTRADOR)) {
            throw new IllegalArgumentException("Solo un técnico o el administrador pueden aceptar una atención.");
        }
        Atencion atencion = atencionRepository.findById(atencionId)
                .orElseThrow(() -> new IllegalArgumentException("Atención no encontrada."));
        if (atencion.getTecnicoAsignado() == null
                || !atencion.getTecnicoAsignado().getId().equals(tecnico.getId())) {
            throw new IllegalStateException("La atención no está asignada a este técnico.");
        }
        if (atencion.getEstado() != EstadoAtencion.ASIGNADA) {
            throw new IllegalStateException("La atención ya fue aceptada o no está disponible.");
        }
        EstadoAtencion anterior = atencion.getEstado();
        atencion.setEstado(EstadoAtencion.EN_REPARACION);
        atencion.setFechaUltimaActualizacion(LocalDateTime.now());
        Atencion actualizada = atencionRepository.save(atencion);
        registrarHistorial(actualizada, anterior, EstadoAtencion.EN_REPARACION,
                "El técnico aceptó la atención y comenzó la reparación.",
                tecnico.getUsername());
        return actualizada;
    }

    @Transactional
    public Atencion marcarRepuestoDisponible(Long atencionId, Usuario usuario) {
        Atencion atencion = atencionRepository.findById(atencionId)
                .orElseThrow(() -> new IllegalArgumentException("Atención no encontrada."));
        if (atencion.getEstado() != EstadoAtencion.ESPERANDO_REPUESTO) {
            throw new IllegalStateException("La atención no está esperando un repuesto.");
        }
        if (usuario == null || (usuario.getRol() != Rol.ADMINISTRADOR
                && (atencion.getTecnicoAsignado() == null
                || !atencion.getTecnicoAsignado().getId().equals(usuario.getId())))) {
            throw new IllegalStateException("Solo el administrador o el técnico asignado puede continuar la atención.");
        }
        EstadoAtencion anterior = atencion.getEstado();
        EstadoAtencion siguiente = atencion.getTecnicoAsignado() != null
                ? EstadoAtencion.ASIGNADA : EstadoAtencion.EN_ESPERA;
        atencion.setEstado(siguiente);
        atencion.setFechaUltimaActualizacion(LocalDateTime.now());
        Atencion actualizada = atencionRepository.save(atencion);
        registrarHistorial(actualizada, anterior, siguiente,
                atencion.getTecnicoAsignado() == null
                        ? "Repuesto disponible. Pendiente de asignar responsable."
                        : "Repuesto disponible. El responsable puede aceptar la atención.",
                usuario.getUsername());
        return actualizada;
    }

    @Transactional
    public Atencion finalizarPorEvidencia(Long atencionId, Usuario usuario) {
        Atencion atencion = atencionRepository.findById(atencionId)
                .orElseThrow(() -> new IllegalArgumentException("Atención no encontrada."));
        if (atencion.getEstado() != EstadoAtencion.EN_REPARACION) {
            throw new IllegalStateException("La atención debe estar en reparación para finalizarla.");
        }
        EstadoAtencion anterior = atencion.getEstado();
        atencion.setEstado(EstadoAtencion.LISTA_PARA_RECOJO);
        atencion.setFechaUltimaActualizacion(LocalDateTime.now());
        Atencion actualizada = atencionRepository.save(atencion);
        registrarHistorial(actualizada, anterior, EstadoAtencion.LISTA_PARA_RECOJO,
                "Atención finalizada con evidencia. Equipo listo para recojo.",
                usuario == null ? "SISTEMA" : usuario.getUsername());
        return actualizada;
    }

    private void registrarHistorial(
            Atencion atencion,
            EstadoAtencion estadoAnterior,
            EstadoAtencion estadoNuevo,
            String comentario,
            String realizadoPor) {

        HistorialEstadoAtencion historial =
                new HistorialEstadoAtencion();

        historial.setAtencion(atencion);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setComentario(comentario);
        historial.setRealizadoPor(realizadoPor);

        historialRepository.save(historial);
    }


}
