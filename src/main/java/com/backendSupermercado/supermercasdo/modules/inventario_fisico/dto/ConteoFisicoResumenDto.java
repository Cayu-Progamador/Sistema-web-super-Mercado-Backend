package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import java.time.LocalDateTime;

public record ConteoFisicoResumenDto(
        long total,
        long contados,
        long pendientes,
        Long diferencias,
        int porcentajeAvance,
        LocalDateTime ultimoGuardado) {
}
