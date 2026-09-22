package com.backendSupermercado.supermercasdo.modules.venta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoVenta;

public record VerificacionVentaDto(
        String codigo,
        LocalDateTime fechaVenta,
        BigDecimal total,
        EstadoVenta estado,
        boolean valida) {
}
