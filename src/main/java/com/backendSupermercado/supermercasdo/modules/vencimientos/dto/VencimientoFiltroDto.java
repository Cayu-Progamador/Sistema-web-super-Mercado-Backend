package com.backendSupermercado.supermercasdo.modules.vencimientos.dto;

import java.time.LocalDate;

public record VencimientoFiltroDto(
        String buscar,
        String estado,
        LocalDate fechaDesde,
        LocalDate fechaHasta) {
}
