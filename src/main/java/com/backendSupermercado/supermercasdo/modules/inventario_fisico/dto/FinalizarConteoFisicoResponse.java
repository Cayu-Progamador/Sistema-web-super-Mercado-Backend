package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import java.time.LocalDateTime;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;

public record FinalizarConteoFisicoResponse(
        Long idInventarioFisico,
        String codigo,
        EstadoInventarioFisico estado,
        LocalDateTime fechaFinalizacion,
        String finalizadoPor,
        ConteoFisicoResumenDto resumen) {
}
