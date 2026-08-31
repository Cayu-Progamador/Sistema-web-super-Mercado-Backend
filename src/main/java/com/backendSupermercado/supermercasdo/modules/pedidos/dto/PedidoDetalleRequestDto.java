package com.backendSupermercado.supermercasdo.modules.pedidos.dto;

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
public class PedidoDetalleRequestDto {

    @NotNull(message = "El producto es obligatorio en cada detalle")
    private Long idProducto;

    @NotNull(message = "La unidad de medida es obligatoria en cada detalle")
    private Long idUnidadMedida;

    @NotNull(message = "La cantidad solicitada es obligatoria en cada detalle")
    @DecimalMin(value = "1.000", message = "La cantidad solicitada debe ser al menos 1")
    private BigDecimal cantidadSolicitada;
}
