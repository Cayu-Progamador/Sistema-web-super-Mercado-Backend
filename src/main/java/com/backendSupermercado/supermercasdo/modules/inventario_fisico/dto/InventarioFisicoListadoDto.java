package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import java.time.LocalDate;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.TipoInventarioFisico;

public record InventarioFisicoListadoDto(
        Long idInventarioFisico,
        String codigo,
        LocalDate fechaProgramada,
        TipoInventarioFisico tipo,
        String tipoLabel,
        String almacen,
        String responsable,
        long productosContados,
        long totalProductos,
        long diferencias,
        EstadoInventarioFisico estado) {
}
