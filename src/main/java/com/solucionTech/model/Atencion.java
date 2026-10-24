package com.solucionTech.model;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

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

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;




@Entity
@Table(name = "atenciones")
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_seguimiento", nullable = false,
            unique = true, length = 30)
    private String codigoSeguimiento;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private LocalDateTime fechaUltimaActualizacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoAtencion estado;

    @Column(nullable = false, length = 50)
    private String tipoEquipo;

    @Column(nullable = false, length = 80)
    private String marca;

    @Column(length = 80)
    private String modelo;

    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    @Column(nullable = false, length = 500)
    private String fallaReportada;

    @Column(length = 500)
    private String accesoriosEntregados;

    @Column(length = 1000)
    private String observacionesRecepcion;

    @Column(length = 500)
    private String diagnosticoInicial;

    @Column(name = "tipo_servicio", length = 30)
    private String tipoServicio;

    @Column(name = "requiere_repuesto")
    private Boolean requiereRepuesto = false;

    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal montoTotal;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnicoAsignado;
    
    @OneToMany(
            mappedBy = "atencion",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Evidencia> evidencias = new ArrayList<>();


    public Atencion() {
    }
    public List<Evidencia> getEvidencias() {
        return evidencias;
    }

    public void setEvidencias(List<Evidencia> evidencias) {
        this.evidencias = evidencias;
    }

    public void agregarEvidencia(Evidencia evidencia) {
        evidencias.add(evidencia);
        evidencia.setAtencion(this);
    }

    public void quitarEvidencia(Evidencia evidencia) {
        evidencias.remove(evidencia);
        evidencia.setAtencion(null);
    }
    @OneToMany(
            mappedBy = "atencion",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    
    private List<HistorialEstadoAtencion> historialEstados =
            new ArrayList<>();
    
    public List<HistorialEstadoAtencion> getHistorialEstados() {
        return historialEstados;
    }

    public void setHistorialEstados(
            List<HistorialEstadoAtencion> historialEstados) {
        this.historialEstados = historialEstados;
    }

    public void agregarHistorial(
            HistorialEstadoAtencion historial) {
        historialEstados.add(historial);
        historial.setAtencion(this);
    }
    @OneToMany(
            mappedBy = "atencion",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RepuestoUtilizado> repuestosUtilizados =
            new ArrayList<>();
    
    public List<RepuestoUtilizado> getRepuestosUtilizados() {
        return repuestosUtilizados;
    }

    public void setRepuestosUtilizados(
            List<RepuestoUtilizado> repuestosUtilizados) {
        this.repuestosUtilizados = repuestosUtilizados;
    }

    public void agregarRepuestoUtilizado(
            RepuestoUtilizado repuestoUtilizado) {

        repuestosUtilizados.add(repuestoUtilizado);
        repuestoUtilizado.setAtencion(this);
    }


    @PrePersist
    public void prepararRegistro() {
        if (codigoSeguimiento == null || codigoSeguimiento.isBlank()) {
            String anio = String.valueOf(Year.now().getValue());
            String codigo = UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

            codigoSeguimiento = "ST-" + anio + "-" + codigo;
        }

        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }

        if (fechaUltimaActualizacion == null) {
            fechaUltimaActualizacion = fechaRegistro;
        }

        if (estado == null) {
            estado = EstadoAtencion.EN_ESPERA;
        }

        if (montoTotal == null) {
            montoTotal = java.math.BigDecimal.ZERO;
        }

        if (tipoServicio == null || tipoServicio.isBlank()) {
            tipoServicio = "REPARACION";
        }

        if (requiereRepuesto == null) {
            requiereRepuesto = false;
        }
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoSeguimiento() {
        return codigoSeguimiento;
    }

    public void setCodigoSeguimiento(String codigoSeguimiento) {
        this.codigoSeguimiento = codigoSeguimiento;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public void setFechaUltimaActualizacion(
            LocalDateTime fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    public EstadoAtencion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAtencion estado) {
        this.estado = estado;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getFallaReportada() {
        return fallaReportada;
    }

    public void setFallaReportada(String fallaReportada) {
        this.fallaReportada = fallaReportada;
    }

    public String getAccesoriosEntregados() {
        return accesoriosEntregados;
    }

    public void setAccesoriosEntregados(String accesoriosEntregados) {
        this.accesoriosEntregados = accesoriosEntregados;
    }

    public String getObservacionesRecepcion() {
        return observacionesRecepcion;
    }

    public void setObservacionesRecepcion(String observacionesRecepcion) {
        this.observacionesRecepcion = observacionesRecepcion;
    }

    public String getDiagnosticoInicial() {
        return diagnosticoInicial;
    }

    public void setDiagnosticoInicial(String diagnosticoInicial) {
        this.diagnosticoInicial = diagnosticoInicial;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public Boolean getRequiereRepuesto() {
        return requiereRepuesto;
    }

    public void setRequiereRepuesto(Boolean requiereRepuesto) {
        this.requiereRepuesto = requiereRepuesto;
    }

    public java.math.BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(java.math.BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    public void setTecnicoAsignado(Usuario tecnicoAsignado) {
        this.tecnicoAsignado = tecnicoAsignado;
    }
}
