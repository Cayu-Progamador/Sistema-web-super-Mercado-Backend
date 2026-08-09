package com.backendSupermercado.supermercasdo.modules.categorias.dto;

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
public class SubcategoriaRequestDto {

    @NotBlank(message = "El nombre de la subcategoría es obligatorio")
    @Size(max = 100, message = "El nombre de la subcategoría no puede superar 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private Long idCategoria;
}
