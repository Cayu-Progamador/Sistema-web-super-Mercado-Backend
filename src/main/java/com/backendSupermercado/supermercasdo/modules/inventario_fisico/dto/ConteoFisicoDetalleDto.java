package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ConteoFisicoDetalleDto(
        Long idDetalle,
        Long idProducto,
        String codigoProducto,
        String producto,
        String unidad,
        Long idAlmacen,
        String almacen,
        Long idUbicacion,
        String ubicacion,
        Long idLote,
        String lote,
        LocalDate fechaVencimiento,
        BigDecimal cantidadSistema,
        BigDecimal cantidadFisica,
        BigDecimal diferencia,
        boolean contado,
        String motivo,
        String observacion,
        LocalDateTime fechaConteo) {
}
