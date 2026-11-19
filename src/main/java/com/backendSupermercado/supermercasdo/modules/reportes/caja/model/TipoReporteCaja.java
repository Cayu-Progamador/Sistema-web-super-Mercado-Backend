package com.backendSupermercado.supermercasdo.modules.reportes.caja.model;

public enum TipoReporteCaja {
    SESIONES("sesiones-caja", "Reporte de sesiones de caja"),
    MOVIMIENTOS("movimientos-caja", "Reporte de movimientos de caja"),
    CONCILIACION("conciliacion-caja", "Reporte de conciliación de caja"),
    RECAUDACION_METODO("recaudacion-metodo", "Reporte de recaudación por método de pago"),
    VENTAS_SESION("ventas-sesion-caja", "Reporte de ventas por sesión de caja"),
    RENDIMIENTO_CAJERO("rendimiento-cajero", "Reporte de rendimiento por cajero");

    private final String ruta;
    private final String titulo;
    TipoReporteCaja(String ruta, String titulo) { this.ruta = ruta; this.titulo = titulo; }
    public String ruta() { return ruta; }
    public String titulo() { return titulo; }
    public static TipoReporteCaja desdeRuta(String ruta) {
        for (TipoReporteCaja tipo : values()) if (tipo.ruta.equalsIgnoreCase(ruta)) return tipo;
        throw new IllegalArgumentException("Tipo de reporte de caja no válido: " + ruta);
    }
}
