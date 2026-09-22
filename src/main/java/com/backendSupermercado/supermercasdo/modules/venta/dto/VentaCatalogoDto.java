package com.backendSupermercado.supermercasdo.modules.venta.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VentaCatalogoDto(
        Long idExistencia,
        Long idProducto,
        String codigoProducto,
        String nombreProducto,
        Long idPresentacion,
        String nombrePresentacion,
        String codigoBarra,
        String unidad,
        BigDecimal factorConversion,
        BigDecimal precioVenta,
        BigDecimal stockDisponible,
        String codigoLote,
        LocalDate fechaVencimiento,
        String almacen,
        String ubicacion,
        boolean presentacionPrincipal) {
}
