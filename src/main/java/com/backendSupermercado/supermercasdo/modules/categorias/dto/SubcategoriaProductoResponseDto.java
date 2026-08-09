package com.backendSupermercado.supermercasdo.modules.categorias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubcategoriaProductoResponseDto {
    private Long idProducto;
    private String nombre;
    private String descripcion;
    private String imagen;
    private Boolean estado;
}
