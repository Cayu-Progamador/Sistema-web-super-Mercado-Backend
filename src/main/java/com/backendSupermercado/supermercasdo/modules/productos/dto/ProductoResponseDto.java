package com.backendSupermercado.supermercasdo.modules.productos.dto;

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
public class ProductoResponseDto {

    private Long idProducto;
    private String codigoInterno;
    private String nombre;
    private String descripcion;
    private Boolean controlaLote;
    private Boolean controlaVencimiento;
    private String imagen;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    private Long idSubcategoria;
    private String nombreSubcategoria;
    private Long idCategoria;
    private String nombreCategoria;

    private Long idMarca;
    private String nombreMarca;

    private Long idUnidadBase;
    private String nombreUnidadBase;
    private String abreviaturaUnidadBase;

    private List<PresentacionProductoResponseDto> presentaciones;
}