package com.solucionTech.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solucionTech.model.Producto;
import com.solucionTech.model.TipoProducto;
import com.solucionTech.repository.ProductoRepository;
import com.solucionTech.repository.DetalleCompraRepository;
import com.solucionTech.repository.DetalleVentaRepository;
import com.solucionTech.repository.MovimientoStockRepository;
import com.solucionTech.repository.RepuestoUtilizadoRepository;

@Service
public class ProductoService {

    private final RepuestoUtilizadoRepository repuestoUtilizadoRepository;
	private final MovimientoStockRepository movimientoStockRepository;
	private final DetalleVentaRepository detalleVentaRepository;
	private final DetalleCompraRepository detalleCompraRepository;
	private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository, DetalleCompraRepository detalleCompraRepository, DetalleVentaRepository detalleVentaRepository, MovimientoStockRepository movimientoStockRepository, RepuestoUtilizadoRepository repuestoUtilizadoRepository) {
        this.productoRepository = productoRepository;
		this.detalleCompraRepository = detalleCompraRepository;
		this.detalleVentaRepository = detalleVentaRepository;
		this.movimientoStockRepository = movimientoStockRepository;
		this.repuestoUtilizadoRepository = repuestoUtilizadoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarActivos() {
        return productoRepository.findByEstadoTrueOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarPorTipo(TipoProducto tipo) {
        return productoRepository
                .findByTipoProductoAndEstadoTrueOrderByNombreAsc(tipo);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarStockBajo() {
        return productoRepository
                .findByStockActualLessThanEqualAndEstadoTrue(0);
    }

    @Transactional
    public Producto registrar(Producto producto) {

        validarProducto(producto);

        // El sistema genera automáticamente el código único.
        producto.setCodigo(
                generarCodigo(producto.getTipoProducto()));

        // Todo producto nuevo inicia con stock cero.
        producto.setStockActual(0);

        if (producto.getStockMinimo() == null) {
            producto.setStockMinimo(0);
        }

        if (producto.getEstado() == null) {
            producto.setEstado(true);
        }

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(Producto producto) {

        if (producto.getId() == null) {
            throw new IllegalArgumentException(
                    "Producto no encontrado.");
        }

        Producto existente = productoRepository
                .findById(producto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto no encontrado."));

        validarProducto(producto);

        // El código original no cambia al editar.
        producto.setCodigo(existente.getCodigo());

        // El stock actual se conserva.
        producto.setStockActual(existente.getStockActual());

        // Se conservan los campos obligatorios.
        if (producto.getEstado() == null) {
            producto.setEstado(existente.getEstado());
        }

        if (producto.getFechaRegistro() == null) {
            producto.setFechaRegistro(
                    existente.getFechaRegistro());
        }

        return productoRepository.save(producto);
    }

    @Transactional
    public void desactivar(Long id) {

        Producto producto = productoRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto no encontrado."));

        producto.setEstado(false);
        productoRepository.save(producto);
    }

    @Transactional
    public void activar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto no encontrado."));
        producto.setEstado(true);
        productoRepository.save(producto);
    }

    @Transactional
    public void eliminarDefinitivamente(Long id) {

        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Producto no encontrado.");
        }

        if (detalleCompraRepository.existsByProductoId(id)
                || detalleVentaRepository.existsByProductoId(id)
                || movimientoStockRepository.existsByProductoId(id)
                || repuestoUtilizadoRepository.existsByProductoId(id)) {

            throw new IllegalStateException(
                    "No se puede eliminar definitivamente: "
                    + "el producto tiene compras, ventas, movimientos "
                    + "o reparaciones históricas relacionadas.");
        }

        productoRepository.deleteById(id);
    }


    private String generarCodigo(TipoProducto tipoProducto) {

        String prefijo;

        if (tipoProducto == TipoProducto.REPUESTO) {
            prefijo = "REP";
        } else {
            prefijo = "ACC";
        }

        String codigo;

        do {
            codigo = prefijo
                    + "-"
                    + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();

        } while (productoRepository.existsByCodigo(codigo));

        return codigo;
    }

    private void validarProducto(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto es obligatorio.");
        }

        if (producto.getNombre() == null
                || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio.");
        }

        if (producto.getTipoProducto() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar el tipo de producto.");
        }

        if (producto.getPrecioCompra() == null
                || producto.getPrecioCompra().signum() < 0) {
            throw new IllegalArgumentException(
                    "El precio de compra no puede ser negativo.");
        }

        if (producto.getPrecioVenta() == null
                || producto.getPrecioVenta().signum() < 0) {
            throw new IllegalArgumentException(
                    "El precio de venta no puede ser negativo.");
        }

        if (producto.getCategoria() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una categoría.");
        }
    }
}
