package com.solucionTech.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solucionTech.model.Atencion;
import com.solucionTech.model.EstadoAtencion;
import com.solucionTech.model.Producto;
import com.solucionTech.model.RepuestoUtilizado;
import com.solucionTech.model.TipoMovimiento;
import com.solucionTech.model.Usuario;
import com.solucionTech.repository.AtencionRepository;
import com.solucionTech.repository.ProductoRepository;
import com.solucionTech.repository.RepuestoUtilizadoRepository;

@Service
public class RepuestoUtilizadoService {

    private final RepuestoUtilizadoRepository repuestoRepository;
    private final AtencionRepository atencionRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoStockService movimientoStockService;

    public RepuestoUtilizadoService(
            RepuestoUtilizadoRepository repuestoRepository,
            AtencionRepository atencionRepository,
            ProductoRepository productoRepository,
            MovimientoStockService movimientoStockService) {

        this.repuestoRepository = repuestoRepository;
        this.atencionRepository = atencionRepository;
        this.productoRepository = productoRepository;
        this.movimientoStockService = movimientoStockService;
    }

    @Transactional
    public RepuestoUtilizado registrar(
            Long atencionId,
            Long productoId,
            Integer cantidad,
            Usuario usuario) {

        validarCantidad(cantidad);

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario responsable es obligatorio.");
        }

        Atencion atencion = atencionRepository.findById(atencionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atención no encontrada."));

        if (atencion.getEstado()
                != EstadoAtencion.EN_REPARACION) {

            throw new IllegalStateException(
                    "La atención debe estar en reparación "
                    + "para utilizar repuestos.");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto no encontrado."));

        if (!Boolean.TRUE.equals(producto.getEstado())) {
            throw new IllegalStateException(
                    "El producto está inactivo.");
        }

        if (producto.getTipoProducto() == null
                || !producto.getTipoProducto().name()
                        .equals("REPUESTO")) {

            throw new IllegalArgumentException(
                    "Solo se pueden utilizar productos "
                    + "clasificados como repuestos.");
        }

        movimientoStockService.registrarSalida(
                productoId,
                cantidad,
                TipoMovimiento.SALIDA_REPARACION,
                "Repuesto utilizado en atención "
                        + atencion.getCodigoSeguimiento(),
                usuario);

        RepuestoUtilizado repuesto =
                new RepuestoUtilizado();

        repuesto.setAtencion(atencion);
        repuesto.setProducto(producto);
        repuesto.setUsuario(usuario);
        repuesto.setCantidad(cantidad);
        repuesto.setPrecioUnitario(producto.getPrecioVenta());
        repuesto.calcularImporte();

        return repuestoRepository.save(repuesto);
    }

    @Transactional(readOnly = true)
    public List<RepuestoUtilizado> listarPorAtencion(
            Long atencionId) {

        Atencion atencion = atencionRepository.findById(atencionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atención no encontrada."));

        return repuestoRepository
                .findByAtencionOrderByFechaDesc(atencion);
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }
    }
}
