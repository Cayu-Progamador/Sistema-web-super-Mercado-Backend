package com.backendSupermercado.supermercasdo.modules.almacen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AlmacenCreadoDto {
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
}
