package com.solucionTech.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.solucionTech.model.Compra;
import com.solucionTech.model.EstadoCompra;
import com.solucionTech.model.EstadoVenta;
import com.solucionTech.model.Producto;
import com.solucionTech.model.ResumenReporte;
import com.solucionTech.model.Venta;
import com.solucionTech.repository.AtencionRepository;
import com.solucionTech.repository.CompraRepository;
import com.solucionTech.repository.ProductoRepository;
import com.solucionTech.repository.VentaRepository;

@Service
public class ReporteService {
    private final ProductoRepository productoRepository;
    private final CompraRepository compraRepository;
    private final VentaRepository ventaRepository;
    private final AtencionRepository atencionRepository;
    public ReporteService(ProductoRepository productoRepository,
            CompraRepository compraRepository, VentaRepository ventaRepository,
            AtencionRepository atencionRepository) {
        this.productoRepository = productoRepository;
        this.compraRepository = compraRepository;
        this.ventaRepository = ventaRepository;
        this.atencionRepository = atencionRepository;
    }
    @Transactional(readOnly = true)
    public ResumenReporte obtenerResumen() { return obtenerResumen(LocalDate.now(), LocalDate.now()); }
    @Transactional(readOnly = true)
    public ResumenReporte obtenerResumen(LocalDate desde, LocalDate hasta) {
        List<Compra> compras = listarComprasConfirmadas(desde, hasta);
        List<Venta> ventas = listarVentasConfirmadas(desde, hasta);
        ResumenReporte r = new ResumenReporte();
        r.setTotalProductos(productoRepository.count());
        r.setTotalCompras(compraRepository.count());
        r.setTotalVentas(ventaRepository.count());
        r.setTotalAtenciones(atencionRepository.count());
        r.setProductosStockBajo(listarStockBajo().size());
        r.setComprasConfirmadas(compras.size());
        r.setVentasConfirmadas(ventas.size());
        r.setTotalComprasConfirmadas(compras.stream().map(Compra::getTotal).filter(x -> x != null).reduce(BigDecimal.ZERO, BigDecimal::add));
        r.setTotalVentasConfirmadas(ventas.stream().map(Venta::getTotal).filter(x -> x != null).reduce(BigDecimal.ZERO, BigDecimal::add));
        return r;
    }
    @Transactional(readOnly = true)
    public List<Compra> listarComprasConfirmadas(LocalDate desde, LocalDate hasta) {
        return compraRepository.findByEstadoAndFechaCompraBetween(EstadoCompra.CONFIRMADA,
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay().minusNanos(1));
    }
    @Transactional(readOnly = true)
    public List<Venta> listarVentasConfirmadas(LocalDate desde, LocalDate hasta) {
        return ventaRepository.findByEstadoAndFechaVentaBetween(EstadoVenta.CONFIRMADA,
                desde.atStartOfDay(), hasta.plusDays(1).atStartOfDay().minusNanos(1));
    }
    @Transactional(readOnly = true)
    public List<Producto> listarStockBajo() {
        return productoRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getEstado()))
                .filter(p -> p.getStockActual() != null && p.getStockMinimo() != null
                        && p.getStockActual() <= p.getStockMinimo())
                .sorted((a,b) -> Integer.compare(a.getStockActual(), b.getStockActual()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Producto> listarInventario() {
        return productoRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getEstado()))
                .sorted((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()))
                .collect(Collectors.toList());
    }
}
