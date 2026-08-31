package com.backendSupermercado.supermercasdo.modules.recepcion.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistribucionRequestDto {

    @NotNull(message = "La ubicación de destino es obligatoria")
    private Long idUbicacion;

    @NotNull(message = "La cantidad a distribuir es obligatoria")
    @DecimalMin(value = "0.000", message = "La cantidad a distribuir no puede ser negativa")
    private BigDecimal cantidad;
}