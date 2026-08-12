package com.backendSupermercado.supermercasdo.modules.productos.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
public class ProductoRequestDto {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 150, message = "El nombre del producto no puede superar 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;

    @NotNull(message = "La subcategoría es obligatoria")
    private Long idSubcategoria;

    private Long idMarca;

    @NotNull(message = "La unidad base es obligatoria")
    private Long idUnidadBase;

    @Size(max = 50, message = "El código interno no puede superar 50 caracteres")
    private String codigoInterno;

    @Builder.Default
    private Boolean controlaLote = false;

    @Builder.Default
    private Boolean controlaVencimiento = false;

    private String imagen;

    @NotNull(message = "El producto debe tener al menos una presentación de venta")
    @Valid
    private List<PresentacionProductoRequestDto> presentaciones;
}
