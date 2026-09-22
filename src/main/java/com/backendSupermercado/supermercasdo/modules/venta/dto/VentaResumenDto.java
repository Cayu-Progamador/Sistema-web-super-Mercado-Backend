package com.backendSupermercado.supermercasdo.modules.venta.dto;

import java.math.BigDecimal;

public record VentaResumenDto(
        long totalVentas,
        BigDecimal totalVendido,
        long pendientesCobro,
        BigDecimal gananciaDiaria) {
}
