package com.backendSupermercado.supermercasdo.modules.recepcion.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
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
public class DetalleRecepcionRequestDto {

    @NotNull(message = "El detalle del pedido es obligatorio")
    private Long idDetallePedido;

    @NotNull(message = "La cantidad recibida es obligatoria")
    @DecimalMin(value = "0.000", message = "La cantidad recibida no puede ser negativa")
    private BigDecimal cantidadRecibida;

    @NotNull(message = "La cantidad aceptada es obligatoria")
    @DecimalMin(value = "0.000", message = "La cantidad aceptada no puede ser negativa")
    private BigDecimal cantidadAceptada;

    @NotNull(message = "La cantidad rechazada es obligatoria")
    @DecimalMin(value = "0.000", message = "La cantidad rechazada no puede ser negativa")
    private BigDecimal cantidadRechazada;

    private Long idMotivoDevolucion;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio unitario no puede ser negativo")
    private BigDecimal precioUnitario;

    @Valid
    @NotNull(message = "Debe indicar al menos una ubicación de distribución cuando acepta mercadería")
    private List<DistribucionRequestDto> distribuciones = new ArrayList<>();

    private LoteRequestDto lote;
}