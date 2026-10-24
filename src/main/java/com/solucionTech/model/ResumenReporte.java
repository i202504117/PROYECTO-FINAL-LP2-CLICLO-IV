package com.solucionTech.model;
import java.math.BigDecimal;


public class ResumenReporte {

    private long totalProductos;
    private long totalCompras;
    private long totalVentas;
    private long totalAtenciones;
    private long productosStockBajo;
    private long comprasConfirmadas;
    private long ventasConfirmadas;
    private BigDecimal totalComprasConfirmadas = BigDecimal.ZERO;
    private BigDecimal totalVentasConfirmadas = BigDecimal.ZERO;


    public ResumenReporte() {
    }

    public long getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(long totalProductos) {
        this.totalProductos = totalProductos;
    }

    public long getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(long totalCompras) {
        this.totalCompras = totalCompras;
    }

    public long getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(long totalVentas) {
        this.totalVentas = totalVentas;
    }

    public long getTotalAtenciones() {
        return totalAtenciones;
    }

    public void setTotalAtenciones(long totalAtenciones) {
        this.totalAtenciones = totalAtenciones;
    }

    public long getProductosStockBajo() {
        return productosStockBajo;
    }

    public void setProductosStockBajo(long productosStockBajo) {
        this.productosStockBajo = productosStockBajo;
    }
    public long getComprasConfirmadas() {
        return comprasConfirmadas;
    }

    public void setComprasConfirmadas(long comprasConfirmadas) {
        this.comprasConfirmadas = comprasConfirmadas;
    }

    public long getVentasConfirmadas() {
        return ventasConfirmadas;
    }

    public void setVentasConfirmadas(long ventasConfirmadas) {
        this.ventasConfirmadas = ventasConfirmadas;
    }

    public BigDecimal getTotalComprasConfirmadas() {
        return totalComprasConfirmadas;
    }

    public void setTotalComprasConfirmadas(
            BigDecimal totalComprasConfirmadas) {

        this.totalComprasConfirmadas = totalComprasConfirmadas;
    }

    public BigDecimal getTotalVentasConfirmadas() {
        return totalVentasConfirmadas;
    }

    public void setTotalVentasConfirmadas(
            BigDecimal totalVentasConfirmadas) {

        this.totalVentasConfirmadas = totalVentasConfirmadas;
    }

}
