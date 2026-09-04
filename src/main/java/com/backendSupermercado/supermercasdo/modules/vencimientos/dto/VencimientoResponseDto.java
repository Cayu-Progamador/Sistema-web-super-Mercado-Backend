package com.backendSupermercado.supermercasdo.modules.vencimientos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VencimientoResponseDto(
        Long idExistencia,
        Long idProducto,
        String codigoProducto,
        String producto,
        Long idLote,
        String lote,
        LocalDate fechaVencimiento,
        long diasRestantes,
        BigDecimal cantidad,
        String unidad,
        String almacen,
        String ubicacion,
        String estado) {
}
