package com.backendSupermercado.supermercasdo.modules.reportes.venta.dto;

import java.util.List;

public record ReporteVentaExportacion(
        String titulo,
        String filtros,
        List<String> columnas,
        List<List<String>> filas) {
}
