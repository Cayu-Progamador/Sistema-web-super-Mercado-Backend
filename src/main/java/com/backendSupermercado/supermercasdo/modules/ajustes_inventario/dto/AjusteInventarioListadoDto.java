package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.EstadoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.MotivoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.TipoAjusteInventario;

public record AjusteInventarioListadoDto(
        Long id,
        String codigo,
        TipoAjusteInventario tipo,
        MotivoAjusteInventario motivo,
        EstadoAjusteInventario estado,
        LocalDateTime fechaCreacion,
        String creadoPor,
        long productos,
        BigDecimal cantidadTotal,
        BigDecimal valorTotal,
        String almacen,
        String ubicacion) {
}
