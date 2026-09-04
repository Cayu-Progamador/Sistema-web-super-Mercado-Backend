package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;
import java.time.LocalDateTime;

public record InventarioFisicoEstadoResponse(
        Long idInventarioFisico,
        String codigo,
        EstadoInventarioFisico estado,
        LocalDateTime fecha,
        String usuario,
        String mensaje) {
}
