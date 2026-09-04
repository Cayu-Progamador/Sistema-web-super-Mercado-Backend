package com.backendSupermercado.supermercasdo.modules.vencimientos.dto;

public record VencimientoResumenDto(
        long vencidos,
        long vencenHoy,
        long proximos7Dias,
        long proximos30Dias) {
}
