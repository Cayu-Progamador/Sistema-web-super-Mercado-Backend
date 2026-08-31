package com.backendSupermercado.supermercasdo.modules.recepcion.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistribuirRecepcionRequestDto {

    @Valid
    @NotNull(message = "Debe incluir al menos un detalle a distribuir")
    private List<ItemDistribucionDto> items = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDistribucionDto {

        @NotNull(message = "El detalle de recepción es obligatorio")
        private Long idDetalleRecepcion;

        @Valid
        @NotNull(message = "Debe indicar al menos una ubicación de distribución")
        private List<DistribucionRequestDto> distribuciones = new ArrayList<>();
    }
}