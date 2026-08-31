package com.backendSupermercado.supermercasdo.modules.transferencias_almacen.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TransferenciaListadoDto(
        Long idTransferencia,
        String codigo,
        LocalDateTime fechaTransferencia,
        Long idAlmacenOrigen,
        Long idAlmacenDestino,
        Long idUbicacionOrigen,
        Long idUbicacionDestino,
        String origen,
        String destino,
        Long cantidadProductos,
        BigDecimal cantidadTotal,
        String estado,
        String responsable,
        String observacion,
        List<DetalleDto> detalles) {
    public record DetalleDto(Long idProducto, Long idLote, String producto, String lote, BigDecimal cantidad) { }
}
