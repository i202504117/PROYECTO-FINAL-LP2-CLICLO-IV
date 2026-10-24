package com.solucionTech.model;

public enum EstadoAtencion {

    EN_ESPERA("Equipo recibido. Pendiente de asignación."),
    ASIGNADA("Un técnico ha sido asignado."),
    ESPERANDO_REPUESTO("Atención pendiente de conseguir el repuesto necesario."),
    EN_DIAGNOSTICO("El técnico está revisando el equipo."),
    ESPERANDO_APROBACION(
            "El diagnóstico está listo. Esperamos su autorización."),
    EN_REPARACION("La reparación está en proceso."),
    LISTA_PARA_RECOJO("Su equipo está listo para recoger."),
    ENTREGADA("El equipo fue entregado."),
    CANCELADA("La atención fue cancelada.");

    private final String descripcion;

    EstadoAtencion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
