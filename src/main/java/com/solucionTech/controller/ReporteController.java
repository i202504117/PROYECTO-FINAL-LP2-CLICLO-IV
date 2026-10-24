package com.solucionTech.controller;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.solucionTech.service.JasperReportService;
import com.solucionTech.service.ReporteService;

@Controller
@RequestMapping("/reportes")
public class ReporteController {
    private final ReporteService reporteService;
    private final JasperReportService jasperReportService;
    public ReporteController(ReporteService reporteService, JasperReportService jasperReportService) {
        this.reporteService = reporteService; this.jasperReportService = jasperReportService;
    }
    @GetMapping
    public String mostrarDashboard(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta, Model model) {
        if (desde == null) desde = LocalDate.now(); if (hasta == null) hasta = desde;
        if (hasta.isBefore(desde)) { model.addAttribute("error", "La fecha final no puede ser anterior a la fecha inicial."); hasta = desde; }
        model.addAttribute("resumen", reporteService.obtenerResumen(desde, hasta));
        model.addAttribute("comprasPeriodo", reporteService.listarComprasConfirmadas(desde, hasta));
        model.addAttribute("ventasPeriodo", reporteService.listarVentasConfirmadas(desde, hasta));
        model.addAttribute("stockBajo", reporteService.listarStockBajo());
        model.addAttribute("inventario", reporteService.listarInventario());
        model.addAttribute("desde", desde); model.addAttribute("hasta", hasta);
        return "reportes/dashboard";
    }
    @GetMapping("/ventas/pdf") public ResponseEntity<byte[]> ventasPdf(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta) throws Exception { return pdf(jasperReportService.ventasPdf(desde,hasta), "reporte-ventas.pdf"); }
    @GetMapping("/compras/pdf") public ResponseEntity<byte[]> comprasPdf(@RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde, @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta) throws Exception { return pdf(jasperReportService.comprasPdf(desde,hasta), "reporte-compras.pdf"); }
    @GetMapping("/stock-bajo/pdf") public ResponseEntity<byte[]> stockBajoPdf() throws Exception { return pdf(jasperReportService.stockBajoPdf(), "reporte-stock-bajo.pdf"); }
    @GetMapping("/inventario/pdf") public ResponseEntity<byte[]> inventarioPdf() throws Exception { return pdf(jasperReportService.inventarioPdf(), "reporte-inventario-general.pdf"); }
    private ResponseEntity<byte[]> pdf(byte[] contenido, String nombre) {
        HttpHeaders headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(nombre).build());
        return ResponseEntity.ok().headers(headers).body(contenido);
    }
}
