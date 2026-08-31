package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class UbicacionListadoDto {
    private Long idUbicacion; private String codigo; private String nombre; private String descripcion;
    private Long idAlmacen; private String nombreAlmacen; private String tipo; private Boolean activo;
    private Long productos; private BigDecimal stockTotal;
}
