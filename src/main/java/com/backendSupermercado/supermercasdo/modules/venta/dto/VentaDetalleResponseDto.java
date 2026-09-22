package com.backendSupermercado.supermercasdo.modules.venta.dto;

import java.math.BigDecimal;

public record VentaDetalleResponseDto(
        String codigoProducto,
        String producto,
        String unidad,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal importe) {
}
