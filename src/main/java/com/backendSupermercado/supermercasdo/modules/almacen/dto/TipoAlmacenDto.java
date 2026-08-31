package com.backendSupermercado.supermercasdo.modules.almacen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TipoAlmacenDto {
    private Long idTipoAlmacen;
    private String nombre;
    private String descripcion;
}
