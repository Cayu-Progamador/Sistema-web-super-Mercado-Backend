package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.EstadoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.MotivoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.TipoAjusteInventario;

public record AjusteInventarioResponse(
        Long id,
        String codigo,
        TipoAjusteInventario tipo,
        MotivoAjusteInventario motivo,
        EstadoAjusteInventario estado,
        String observacion,
        String creadoPor,
        String aprobadoPor,
        String aplicadoPor,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaAprobacion,
        LocalDateTime fechaAplicacion,
        LocalDate fechaLevantamiento,
        String responsableLevantamiento,
        String lugarHallazgo,
        LocalDate fechaHallazgo,
        String encontradoPor,
        BigDecimal cantidadTotal,
        BigDecimal valorTotal,
        List<DetalleResponse> detalles) {

    public record DetalleResponse(
            Long id,
            Long idProducto,
            String codigoProducto,
            String producto,
            String unidad,
            Long idLote,
            String codigoLote,
            LocalDate fechaFabricacion,
            LocalDate fechaVencimiento,
            Long idAlmacen,
            String almacen,
            Long idUbicacion,
            String ubicacion,
            BigDecimal stockAnterior,
            BigDecimal cantidad,
            BigDecimal stockResultante,
            BigDecimal precioVenta,
            BigDecimal costoUnitario,
            BigDecimal valorTotal,
            String tipoMerma,
            String causaMerma) {
    }
}
