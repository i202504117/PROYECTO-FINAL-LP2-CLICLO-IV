package com.solucionTech.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solucionTech.model.Compra;
import com.solucionTech.model.DetalleCompra;
import com.solucionTech.model.EstadoCompra;
import com.solucionTech.model.Producto;
import com.solucionTech.model.Usuario;
import com.solucionTech.repository.CompraRepository;
import com.solucionTech.repository.ProductoRepository;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoStockService movimientoStockService;

    public CompraService(
            CompraRepository compraRepository,
            ProductoRepository productoRepository,
            MovimientoStockService movimientoStockService) {

        this.compraRepository = compraRepository;
        this.productoRepository = productoRepository;
        this.movimientoStockService = movimientoStockService;
    }

    @Transactional(readOnly = true)
    public List<Compra> listarTodas() {
        return compraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Compra buscarPorId(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Compra no encontrada."));
    }

    @Transactional
    public Compra registrar(Compra compra) {
        validarCompra(compra);

        if (compra.getNumeroCompra() == null
                || compra.getNumeroCompra().isBlank()) {
            compra.setNumeroCompra(generarNumeroCompra());
        }

        if (compraRepository.existsByNumeroCompra(
                compra.getNumeroCompra())) {
            throw new IllegalArgumentException(
                    "El número de compra ya existe.");
        }

        compra.setEstado(EstadoCompra.REGISTRADA);
        calcularTotal(compra);

        return compraRepository.save(compra);
    }

    @Transactional
    public Compra confirmar(Long compraId, Usuario usuario) {
        Compra compra = buscarPorId(compraId);

        if (compra.getEstado() != EstadoCompra.REGISTRADA) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar compras registradas.");
        }

        validarCompra(compra);

        for (DetalleCompra detalle : compra.getDetalles()) {
            Producto producto = detalle.getProducto();

            if (producto == null || producto.getId() == null) {
                throw new IllegalArgumentException(
                        "Cada detalle debe tener un producto.");
            }

            movimientoStockService.registrarEntrada(
                    producto.getId(),
                    detalle.getCantidad(),
                    "Entrada por compra "
                            + compra.getNumeroCompra(),
                    usuario);
        }

        compra.setEstado(EstadoCompra.CONFIRMADA);

        return compraRepository.save(compra);
    }

    @Transactional
    public Compra anular(Long compraId) {
        Compra compra = buscarPorId(compraId);

        if (compra.getEstado() != EstadoCompra.REGISTRADA) {
            throw new IllegalStateException(
                    "Solo se pueden anular compras registradas.");
        }

        compra.setEstado(EstadoCompra.ANULADA);

        return compraRepository.save(compra);
    }

    private void calcularTotal(Compra compra) {
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleCompra detalle : compra.getDetalles()) {
            detalle.calcularImporte();
            total = total.add(detalle.getImporte());
            detalle.setCompra(compra);
        }

        compra.setTotal(total);
    }

    private void validarCompra(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException(
                    "La compra es obligatoria.");
        }

        if (compra.getProveedor() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un proveedor.");
        }

        if (compra.getUsuario() == null) {
            throw new IllegalArgumentException(
                    "La compra debe tener un usuario responsable.");
        }

        if (compra.getDetalles() == null
                || compra.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "La compra debe tener al menos un detalle.");
        }

        for (DetalleCompra detalle : compra.getDetalles()) {
            validarDetalle(detalle);
        }
    }

    private void validarDetalle(DetalleCompra detalle) {
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

    private String generarNumeroCompra() {
        String fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        return "C-" + fecha;
    }
}
