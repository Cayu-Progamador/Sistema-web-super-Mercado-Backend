package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionAlmacenResponseDto {

    private Long idUbicacion;
    private String codigo;
    private String nombre;
    private Long idAlmacen;
    private String nombreAlmacen;
    private String tipoAlmacen;
}