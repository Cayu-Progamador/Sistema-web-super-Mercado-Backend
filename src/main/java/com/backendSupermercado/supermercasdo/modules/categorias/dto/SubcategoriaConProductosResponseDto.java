package com.backendSupermercado.supermercasdo.modules.categorias.dto;

import java.time.LocalDateTime;
import java.util.List;

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
public class SubcategoriaConProductosResponseDto {
    private Long idSubcategoria;
    private String nombre;
    private String descripcion;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Long idCategoria;
    private String nombreCategoria;
    private List<SubcategoriaProductoResponseDto> productos;
}
