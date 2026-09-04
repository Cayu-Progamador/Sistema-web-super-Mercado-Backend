package com.backendSupermercado.supermercasdo.modules.inventario.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExistenciaResponseDto(Long idExistencia, Long idProducto, String codigoProducto, String nombreProducto,
        Long idUbicacion, String codigoUbicacion, String nombreUbicacion, String almacen,
        Long idLote, String codigoLote, LocalDate fechaVencimiento, BigDecimal cantidad,
        BigDecimal stockMinimo, BigDecimal stockMaximo, String unidadBase, String estado, List<LoteExistenciaDto> lotes) { }
