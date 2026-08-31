package com.backendSupermercado.supermercasdo.modules.devoluciones.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotivoDevolucionResponseDto {

    private Long idMotivoDevolucion;
    private String nombre;
    private String descripcion;
}