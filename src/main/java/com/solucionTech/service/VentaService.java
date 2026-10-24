package com.solucionTech.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solucionTech.model.DetalleVenta;
import com.solucionTech.model.EstadoVenta;
import com.solucionTech.model.Producto;
import com.solucionTech.model.TipoMovimiento;
import com.solucionTech.model.Usuario;
import com.solucionTech.model.Venta;
import com.solucionTech.repository.ProductoRepository;
import com.solucionTech.repository.VentaRepository;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoStockService movimientoStockService;

    public VentaService(
            VentaRepository ventaRepository,
            ProductoRepository productoRepository,
            MovimientoStockService movimientoStockService) {

        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.movimientoStockService = movimientoStockService;
    }

    @Transactional(readOnly = true)
    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Venta no encontrada."));
    }

    @Transactional
    public Venta registrar(Venta venta) {
        validarVenta(venta);

        if (venta.getNumeroVenta() == null
                || venta.getNumeroVenta().isBlank()) {
            venta.setNumeroVenta(generarNumeroVenta());
        }

        if (ventaRepository.existsByNumeroVenta(
                venta.getNumeroVenta())) {
            throw new IllegalArgumentException(
                    "El número de venta ya existe.");
        }

        venta.setEstado(EstadoVenta.REGISTRADA);
        calcularTotal(venta);

        return ventaRepository.save(venta);
    }

    @Transactional
    public Venta confirmar(Long ventaId, Usuario usuario) {
        Venta venta = buscarPorId(ventaId);

        if (venta.getEstado() != EstadoVenta.REGISTRADA) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar ventas registradas.");
        }

        validarVenta(venta);

        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();

            if (producto == null || producto.getId() == null) {
                throw new IllegalArgumentException(
                        "Cada detalle debe tener un producto.");
            }

            movimientoStockService.registrarSalida(
                    producto.getId(),
                    detalle.getCantidad(),
                    TipoMovimiento.SALIDA_VENTA,
                    "Salida por venta "
                            + venta.getNumeroVenta(),
                    usuario);
        }

        venta.setEstado(EstadoVenta.CONFIRMADA);

        return ventaRepository.save(venta);
    }

    @Transactional
    public Venta anular(Long ventaId) {
        Venta venta = buscarPorId(ventaId);

        if (venta.getEstado() != EstadoVenta.REGISTRADA) {
            throw new IllegalStateException(
                    "Solo se pueden anular ventas registradas.");
        }

        venta.setEstado(EstadoVenta.ANULADA);

        return ventaRepository.save(venta);
    }

    private void calcularTotal(Venta venta) {
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.calcularImporte();
            detalle.setVenta(venta);
            total = total.add(detalle.getImporte());
        }

        venta.setTotal(total);
    }

    private void validarVenta(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException(
                    "La venta es obligatoria.");
        }

        if (venta.getUsuario() == null) {
            throw new IllegalArgumentException(
                    "La venta debe tener un usuario responsable.");
        }

        if (venta.getDetalles() == null
                || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "La venta debe tener al menos un detalle.");
        }

        for (DetalleVenta detalle : venta.getDetalles()) {
            validarDetalle(detalle);
        }
    }

    private void validarDetalle(DetalleVenta detalle) {
        if (detalle.getProducto() == null) {
            throw new IllegalArgumentException(
                    "El detalle debe tener un producto.");
        }

        if (detalle.getCantidad() == null
                || detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }

        if (detalle.getPrecioUnitario() == null
                || detalle.getPrecioUnitario().signum() < 0) {
            throw new IllegalArgumentException(
                    "El precio unitario no puede ser negativo.");
        }
    }

    private String generarNumeroVenta() {
        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        return "V-" + fecha;
    }
}
