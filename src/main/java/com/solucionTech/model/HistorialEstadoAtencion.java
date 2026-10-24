package com.solucionTech.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "historial_estados_atencion")
public class HistorialEstadoAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 30)
    private EstadoAtencion estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 30)
    private EstadoAtencion estadoNuevo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 300)
    private String comentario;

    @Column(length = 30)
    private String realizadoPor;

    @ManyToOne
    @JoinColumn(name = "atencion_id", nullable = false)
    private Atencion atencion;

    public HistorialEstadoAtencion() {
    }

    @PrePersist
    public void prepararRegistro() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EstadoAtencion getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoAtencion estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoAtencion getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoAtencion estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getRealizadoPor() {
        return realizadoPor;
    }

    public void setRealizadoPor(String realizadoPor) {
        this.realizadoPor = realizadoPor;
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        this.atencion = atencion;
    }
}
