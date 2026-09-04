package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import org.springframework.data.domain.Page;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;

public record ConteoFisicoPaginaDto(
        Long idInventarioFisico,
        String codigo,
        EstadoInventarioFisico estado,
        boolean conteoCiego,
        Page<ConteoFisicoDetalleDto> detalles,
        ConteoFisicoResumenDto resumen) {
}
