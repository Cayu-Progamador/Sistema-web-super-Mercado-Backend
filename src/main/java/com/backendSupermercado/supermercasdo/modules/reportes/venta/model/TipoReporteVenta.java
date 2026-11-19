package com.backendSupermercado.supermercasdo.modules.reportes.venta.model;

import java.util.Arrays;

public enum TipoReporteVenta {
    GENERAL("general", "Reporte general de ventas"),
    GANANCIAS("ganancias", "Reporte de ganancias por ventas"),
    PRODUCTOS_MAS_VENDIDOS("productos-mas-vendidos", "Reporte de productos más vendidos"),
    PRODUCTOS_MENOS_VENDIDOS("productos-menos-vendidos", "Reporte de productos menos vendidos"),
    PRODUCTOS_SIN_VENTAS("productos-sin-ventas", "Reporte de productos sin ventas"),
    METODOS_PAGO("metodos-pago", "Reporte de ventas por método de pago"),
    CAJEROS("cajeros", "Reporte de ventas por cajero"),
    CONTADO_CREDITO("contado-credito", "Reporte de ventas al contado y a crédito"),
    PERIODOS("periodos", "Reporte de ventas por periodo"),
    CLIENTES("clientes", "Reporte de ventas por cliente");

    private final String ruta;
    private final String titulo;

    TipoReporteVenta(String ruta, String titulo) {
        this.ruta = ruta;
        this.titulo = titulo;
    }

    public String ruta() {
        return ruta;
    }

    public String titulo() {
        return titulo;
    }

    public static TipoReporteVenta desdeRuta(String ruta) {
        return Arrays.stream(values())
                .filter(tipo -> tipo.ruta.equalsIgnoreCase(ruta))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de reporte de ventas no válido: " + ruta));
    }
}
