package com.backendSupermercado.supermercasdo.modules.transferencias_almacen.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;

public record CrearTransferenciaRequest(
        Long idUbicacionOrigen,
        Long idUbicacionDestino,
        String observacion,
        @NotEmpty(message = "Debe agregar al menos un producto")
        List<@Valid DetalleRequest> detalles) {
    public record DetalleRequest(Long idProducto, Long idLote, Long idPresentacion,
            BigDecimal cantidadPresentacion,
            @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor que cero") BigDecimal cantidad) { }
}
