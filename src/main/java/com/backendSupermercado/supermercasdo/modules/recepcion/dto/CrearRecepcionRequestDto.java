package com.backendSupermercado.supermercasdo.modules.recepcion.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearRecepcionRequestDto {

    @NotNull(message = "El pedido es obligatorio")
    private Long idPedido;

    private Long idDevolucionReemplazo;

    @Size(max = 1000, message = "La observación no puede exceder 1000 caracteres")
    private String observacion;

    @Valid
    @NotNull(message = "Debe incluir al menos un detalle de recepción")
    private List<DetalleRecepcionRequestDto> detalles = new ArrayList<>();
}
