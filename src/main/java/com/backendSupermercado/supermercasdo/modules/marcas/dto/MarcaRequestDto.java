package com.backendSupermercado.supermercasdo.modules.marcas.dto;

import jakarta.validation.constraints.NotBlank;
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
public class MarcaRequestDto {

    @NotBlank(message = "El nombre de la marca es obligatorio")
    @Size(max = 100, message = "El nombre de la marca no puede superar 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;
}