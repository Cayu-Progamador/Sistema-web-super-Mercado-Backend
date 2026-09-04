package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.TipoInventarioFisico;

public record InventarioFisicoResponse(
        Long idInventarioFisico,
        String codigo,
        TipoInventarioFisico tipo,
        EstadoInventarioFisico estado,
        LocalDate fechaProgramada,
        Boolean conteoCiego,
        String observacion,
        ItemSimpleDto responsable,
        ItemSimpleDto almacen,
        ItemSimpleDto ubicacion,
        ItemSimpleDto categoria,
        long totalProductos,
        long totalLineas,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFinalizacion,
        LocalDateTime fechaAprobacion,
        ItemSimpleDto creadoPor,
        ConteoFisicoResumenDto resumen,
        List<ConteoFisicoDetalleDto> detalles,
        List<Long> idsProductosSeleccionados) {

    public record ItemSimpleDto(Long id, String nombre) {
    }
}
