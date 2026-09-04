package com.backendSupermercado.supermercasdo.modules.kardex.dto;

import java.time.LocalDate;

public record KardexFiltroDto(
        String buscar,
        Long idProducto,
        Long idLote,
        LocalDate fechaDesde,
        LocalDate fechaHasta) {
}
