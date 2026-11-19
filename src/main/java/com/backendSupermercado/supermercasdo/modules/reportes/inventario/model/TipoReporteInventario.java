package com.backendSupermercado.supermercasdo.modules.reportes.inventario.model;

public enum TipoReporteInventario {
    EXISTENCIAS("existencias", "Reporte de existencias actuales"),
    STOCK_MINIMO("stock-minimo", "Reporte de stock mínimo"),
    AGOTADOS("productos-agotados", "Reporte de productos agotados"),
    PROXIMOS("proximos-vencer", "Reporte de productos próximos a vencer"),
    VENCIDOS("productos-vencidos", "Reporte de productos vencidos"),
    KARDEX("kardex", "Reporte de kardex de producto"),
    TRANSFERENCIAS("transferencias-inventario", "Reporte de transferencias de inventario"),
    INVENTARIO_FISICO("inventario-fisico", "Reporte de inventario físico"),
    AJUSTES("ajustes-inventario", "Reporte de ajustes de inventario");

    private final String ruta;
    private final String titulo;

    TipoReporteInventario(String ruta, String titulo) { this.ruta = ruta; this.titulo = titulo; }
    public String ruta() { return ruta; }
    public String titulo() { return titulo; }

    public static TipoReporteInventario desdeRuta(String ruta) {
        for (TipoReporteInventario tipo : values()) if (tipo.ruta.equalsIgnoreCase(ruta)) return tipo;
        throw new IllegalArgumentException("Reporte de inventario no válido");
    }
}
