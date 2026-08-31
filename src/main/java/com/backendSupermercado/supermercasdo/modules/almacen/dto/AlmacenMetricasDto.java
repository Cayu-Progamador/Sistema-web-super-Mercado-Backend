package com.backendSupermercado.supermercasdo.modules.almacen.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlmacenMetricasDto {
    private Long idAlmacen;
    private Long totalUbicaciones;
    private Long totalProductos;
    private BigDecimal stockTotal;
}
