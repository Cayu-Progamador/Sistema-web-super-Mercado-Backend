package com.backendSupermercado.supermercasdo.modules.unidad_medida.dto;

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
public class UnidadMedidaRequestDto {

    @NotBlank(message = "El nombre de la unidad de medida es obligatorio")
    @Size(max = 100, message = "El nombre de la unidad de medida no puede superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "La abreviatura es obligatoria")
    @Size(max = 20, message = "La abreviatura no puede superar 20 caracteres")
    private String abreviatura;

    @NotNull(message = "El tipo de unidad es obligatorio")
    private Long idTipoUnidad;
}