package com.backendSupermercado.supermercasdo.modules.kardex.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record KardexResponseDto(
        Long id,
        LocalDateTime fecha,
        Long idProducto,
        String codigoProducto,
        String producto,
        String documento,
        String operacion,
        Long idLote,
        String lote,
        String almacen,
        String ubicacion,
        BigDecimal entrada,
        BigDecimal salida,
        BigDecimal saldo,
        String unidad,
        String usuario,
        String observacion) {
}
