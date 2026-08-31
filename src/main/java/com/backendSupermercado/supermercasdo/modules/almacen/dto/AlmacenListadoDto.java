package com.backendSupermercado.supermercasdo.modules.almacen.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlmacenListadoDto {
    private Long idAlmacen;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String direccion;
    private Long idTipoAlmacen;
    private String tipo;
    private Boolean activo;
    private Long idResponsable;
    private String responsable;
    private String telefonoResponsable;
    private Long totalUbicaciones;
    private Long totalProductos;
    private BigDecimal stockTotal;
}
