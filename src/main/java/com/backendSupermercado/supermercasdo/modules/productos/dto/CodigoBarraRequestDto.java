package com.backendSupermercado.supermercasdo.modules.productos.dto;

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
public class CodigoBarraRequestDto {

    @NotBlank(message = "El código de barras es obligatorio")
    @Size(max = 50, message = "El código de barras no puede superar 50 caracteres")
    private String codigo;

    @Builder.Default
    private Boolean principal = false;
}
