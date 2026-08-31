package com.backendSupermercado.supermercasdo.modules.almacen.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponsableAlmacenDto {
    private Long idEmpleado;
    private String nombreCompleto;
    private String telefono;
}
