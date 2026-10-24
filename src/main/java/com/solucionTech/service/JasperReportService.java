package com.solucionTech.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.solucionTech.model.Compra;
import com.solucionTech.model.DetalleCompra;
import com.solucionTech.model.DetalleVenta;
import com.solucionTech.model.Producto;
import com.solucionTech.model.Venta;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Service
public class JasperReportService {
    private final ReporteService reporteService;
    public JasperReportService(ReporteService reporteService) { this.reporteService = reporteService; }

    public byte[] ventasPdf(LocalDate desde, LocalDate hasta) throws Exception {
        List<Map<String,Object>> rows = new ArrayList<>();
        for (Venta venta : reporteService.listarVentasConfirmadas(desde, hasta)) {
            for (DetalleVenta d : venta.getDetalles()) {
                Map<String,Object> row = new HashMap<>();
                row.put("numero", venta.getNumeroVenta()); row.put("fecha", venta.getFechaVenta());
                row.put("cliente", venta.getCliente() == null ? "Venta general" : venta.getCliente().getNombres()+" "+venta.getCliente().getApellidos());
                row.put("producto", d.getProducto().getNombre()); row.put("cantidad", d.getCantidad());
                row.put("precio", d.getPrecioUnitario()); row.put("importe", d.getImporte()); row.put("total", venta.getTotal());
                rows.add(row);
            }
        }
        return generar("reportes/ventas.jrxml", rows, desde, hasta);
    }

    public byte[] comprasPdf(LocalDate desde, LocalDate hasta) throws Exception {
        List<Map<String,Object>> rows = new ArrayList<>();
        for (Compra compra : reporteService.listarComprasConfirmadas(desde, hasta)) {
            for (DetalleCompra d : compra.getDetalles()) {
                Map<String,Object> row = new HashMap<>();
                row.put("numero", compra.getNumeroCompra()); row.put("fecha", compra.getFechaCompra());
                row.put("proveedor", compra.getProveedor().getRazonSocial()); row.put("producto", d.getProducto().getNombre());
                row.put("cantidad", d.getCantidad()); row.put("precio", d.getPrecioUnitario()); row.put("importe", d.getImporte());
                row.put("total", compra.getTotal()); rows.add(row);
            }
        }
        return generar("reportes/compras.jrxml", rows, desde, hasta);
    }

    public byte[] stockBajoPdf() throws Exception {
        List<Map<String,Object>> rows = new ArrayList<>();
        for (Producto p : reporteService.listarStockBajo()) {
            Map<String,Object> row = new HashMap<>(); row.put("codigo", p.getCodigo()); row.put("producto", p.getNombre());
            row.put("categoria", p.getCategoria().getNombre()); row.put("stock", p.getStockActual()); row.put("minimo", p.getStockMinimo()); rows.add(row);
        }
        return generar("reportes/stock-bajo.jrxml", rows, null, null);
    }

    public byte[] inventarioPdf() throws Exception {
        List<Map<String,Object>> rows = new ArrayList<>();
        for (Producto p : reporteService.listarInventario()) {
            Map<String,Object> row = new HashMap<>();
            row.put("codigo", p.getCodigo());
            row.put("producto", p.getNombre());
            row.put("categoria", p.getCategoria().getNombre());
            row.put("proveedor", p.getProveedor() == null
                    ? "Sin proveedor" : p.getProveedor().getRazonSocial());
            row.put("precioCompra", p.getPrecioCompra());
            row.put("precioVenta", p.getPrecioVenta());
            row.put("stock", p.getStockActual());
            row.put("minimo", p.getStockMinimo());
            row.put("valorizacion", p.getPrecioCompra()
                    .multiply(java.math.BigDecimal.valueOf(p.getStockActual())));
            rows.add(row);
        }
        return generar("reportes/inventario.jrxml", rows, null, null);
    }

    private byte[] generar(String ruta, List<Map<String,Object>> rows, LocalDate desde, LocalDate hasta) throws Exception {
        ClassPathResource recurso = new ClassPathResource(ruta);
        try (InputStream in = recurso.getInputStream()) {
            JasperReport reporte = JasperCompileManager.compileReport(in);
            Map<String,Object> parametros = new HashMap<>();
            parametros.put("DESDE", desde == null ? "" : desde.toString()); parametros.put("HASTA", hasta == null ? "" : hasta.toString());
            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, new JRBeanCollectionDataSource(rows));
            return JasperExportManager.exportReportToPdf(print);
        }
    }
}
