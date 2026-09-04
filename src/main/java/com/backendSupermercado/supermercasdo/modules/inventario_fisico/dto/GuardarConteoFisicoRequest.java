package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GuardarConteoFisicoRequest(
        @NotEmpty(message = "Debe enviar al menos un producto contado")
        List<@Valid ItemConteo> detalles) {

    public record ItemConteo(
            @NotNull(message = "El detalle del conteo es obligatorio")
            Long idDetalle,

            @NotNull(message = "La cantidad física es obligatoria")
            @DecimalMin(value = "0.000", message = "La cantidad física no puede ser negativa")
            @Digits(integer = 9, fraction = 3,
                    message = "La cantidad física permite hasta 9 enteros y 3 decimales")
            BigDecimal cantidadFisica,

            @Size(max = 500, message = "La observación no puede superar 500 caracteres")
            String observacion) {
    }
}
