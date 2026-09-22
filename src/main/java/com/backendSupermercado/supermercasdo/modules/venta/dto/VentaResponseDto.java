package com.backendSupermercado.supermercasdo.modules.venta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoVenta;

public record VentaResponseDto(
        Long idVenta,
        String codigo,
        LocalDateTime fechaVenta,
        String cliente,
        String usuario,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal total,
        EstadoVenta estado,
        String metodoPago,
        BigDecimal saldoPendiente,
        Integer cantidadProductos,
        List<VentaDetalleResponseDto> detalles,
        BigDecimal pagoInicial,
        String estadoCuenta,
        LocalDate fechaLimite,
        BigDecimal montoEfectivo,
        BigDecimal montoQr,
        BigDecimal totalRecibido,
        BigDecimal cambio,
        String referenciaQr) {
}
