package com.solucionTech.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "evidencias")
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_archivo", nullable = false, length = 150)
    private String nombreArchivo;

    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;

    @Column(name = "tipo_archivo", nullable = false, length = 50)
    private String tipoArchivo;

    @Column(length = 300)
    private String descripcion;

    @Column(name = "visible_para_cliente", nullable = false)
    private Boolean visibleParaCliente;

    @Column(nullable = false)
    private LocalDateTime fechaSubida;

    @ManyToOne
    @JoinColumn(name = "atencion_id", nullable = false)
    private Atencion atencion;

    public Evidencia() {
    }

    @PrePersist
    public void prepararRegistro() {
        if (visibleParaCliente == null) {
            visibleParaCliente = true;
        }

        if (fechaSubida == null) {
            fechaSubida = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getVisibleParaCliente() {
        return visibleParaCliente;
    }

    public void setVisibleParaCliente(Boolean visibleParaCliente) {
        this.visibleParaCliente = visibleParaCliente;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        this.atencion = atencion;
    }
}
