package com.solucionTech.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solucionTech.model.MovimientoStock;
import com.solucionTech.model.Producto;
import com.solucionTech.model.TipoMovimiento;
import com.solucionTech.model.Usuario;
import com.solucionTech.repository.MovimientoStockRepository;
import com.solucionTech.repository.ProductoRepository;

@Service
public class MovimientoStockService {

    private final MovimientoStockRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    public MovimientoStockService(
            MovimientoStockRepository movimientoRepository,
            ProductoRepository productoRepository) {

        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public MovimientoStock registrarEntrada(
            Long productoId,
            Integer cantidad,
            String motivo,
            Usuario usuario) {

        validarCantidad(cantidad);

        Producto producto = obtenerProducto(productoId);

        Integer stockAnterior = producto.getStockActual();
        Integer stockNuevo = stockAnterior + cantidad;

        producto.setStockActual(stockNuevo);
        productoRepository.save(producto);

        return registrarMovimiento(
                producto,
                TipoMovimiento.ENTRADA_COMPRA,
                cantidad,
                stockAnterior,
                stockNuevo,
                motivo,
                usuario);
    }

    @Transactional
    public MovimientoStock registrarSalida(
            Long productoId,
            Integer cantidad,
            TipoMovimiento tipoMovimiento,
            String motivo,
            Usuario usuario) {

        validarCantidad(cantidad);

        if (tipoMovimiento != TipoMovimiento.SALIDA_VENTA
                && tipoMovimiento
                        != TipoMovimiento.SALIDA_REPARACION) {

            throw new IllegalArgumentException(
                    "El tipo de salida no es válido.");
        }

        Producto producto = obtenerProducto(productoId);

        Integer stockAnterior = producto.getStockActual();

        if (stockAnterior < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente para realizar la salida.");
        }

        Integer stockNuevo = stockAnterior - cantidad;

        producto.setStockActual(stockNuevo);
        productoRepository.save(producto);

        return registrarMovimiento(
                producto,
                tipoMovimiento,
                cantidad,
                stockAnterior,
                stockNuevo,
                motivo,
                usuario);
    }

    @Transactional
    public MovimientoStock registrarAjuste(
            Long productoId,
            Integer cantidad,
            TipoMovimiento tipoMovimiento,
            String motivo,
            Usuario usuario) {

        validarCantidad(cantidad);

        if (tipoMovimiento != TipoMovimiento.AJUSTE_POSITIVO
                && tipoMovimiento
                        != TipoMovimiento.AJUSTE_NEGATIVO) {

            throw new IllegalArgumentException(
                    "El tipo de ajuste no es válido.");
        }

        Producto producto = obtenerProducto(productoId);
        Integer stockAnterior = producto.getStockActual();
        Integer stockNuevo;

        if (tipoMovimiento == TipoMovimiento.AJUSTE_POSITIVO) {
            stockNuevo = stockAnterior + cantidad;
        } else {
            if (stockAnterior < cantidad) {
                throw new IllegalArgumentException(
                        "El ajuste dejaría el stock en negativo.");
            }

            stockNuevo = stockAnterior - cantidad;
        }

        producto.setStockActual(stockNuevo);
        productoRepository.save(producto);

        return registrarMovimiento(
                producto,
                tipoMovimiento,
                cantidad,
                stockAnterior,
                stockNuevo,
                motivo,
                usuario);
    }

    @Transactional(readOnly = true)
    public List<MovimientoStock> listarPorProducto(
            Producto producto) {

        return movimientoRepository
                .findByProductoOrderByFechaDesc(producto);
    }

    private Producto obtenerProducto(Long productoId) {
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto no encontrado."));
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero.");
        }
    }

    private MovimientoStock registrarMovimiento(
            Producto producto,
            TipoMovimiento tipoMovimiento,
            Integer cantidad,
            Integer stockAnterior,
            Integer stockNuevo,
            String motivo,
            Usuario usuario) {

        MovimientoStock movimiento = new MovimientoStock();

        movimiento.setProducto(producto);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setCantidad(cantidad);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockNuevo(stockNuevo);
        movimiento.setMotivo(motivo);
        movimiento.setUsuario(usuario);

        return movimientoRepository.save(movimiento);
    }
}
