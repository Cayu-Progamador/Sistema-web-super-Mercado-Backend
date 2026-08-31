package com.backendSupermercado.supermercasdo.modules.devoluciones.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearDevolucionRequestDto {

    @NotNull(message = "La recepción es obligatoria")
    private Long idRecepcion;

    @Size(max = 1000, message = "La observación no puede exceder 1000 caracteres")
    private String observacion;

    @Valid
    @NotEmpty(message = "Debe incluir al menos un producto")
    private List<DetalleRequestDto> detalles = new ArrayList<>();

    @Getter
    @Setter
    public static class DetalleRequestDto {

        @NotNull(message = "El detalle de recepción es obligatorio")
        private Long idDetalleRecepcion;

        @NotNull(message = "El motivo de devolución es obligatorio")
        private Long idMotivoDevolucion;

        @NotNull(message = "La cantidad a devolver es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad a devolver debe ser mayor que cero")
        private BigDecimal cantidad;

        @Size(max = 255, message = "La observación del producto no puede exceder 255 caracteres")
        private String observacion;

        @Valid
        @NotEmpty(message = "Debe indicar al menos una ubicación de origen")
        private List<OrigenRequestDto> origenes = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class OrigenRequestDto {

        @NotNull(message = "La ubicación de origen es obligatoria")
        private Long idUbicacion;

        @NotNull(message = "La cantidad de la ubicación es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad de la ubicación debe ser mayor que cero")
        private BigDecimal cantidad;
    }
}
