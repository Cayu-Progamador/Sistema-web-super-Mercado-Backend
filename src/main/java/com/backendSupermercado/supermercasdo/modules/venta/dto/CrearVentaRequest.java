package com.backendSupermercado.supermercasdo.modules.venta.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearVentaRequest(
        Long idCliente,
        Long idCaja,
        Long idSesionCaja,
        @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo") BigDecimal descuento,
        @Size(max = 500, message = "La observacion no puede superar 500 caracteres") String observacion,
        @NotEmpty(message = "Debe agregar al menos un producto") List<@Valid DetalleRequest> detalles,
        @NotNull(message = "El metodo de pago es obligatorio") PagoRequest pago) {

    public record DetalleRequest(
        @NotNull(message = "La existencia es obligatoria") Long idExistencia,
            @NotNull(message = "La presentacion es obligatoria") Long idPresentacion,
            @NotNull(message = "La cantidad es obligatoria")
            @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor que cero") BigDecimal cantidad,
            @NotNull(message = "El precio unitario es obligatorio")
            @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor que cero") BigDecimal precioUnitario) {
    }

    public record PagoRequest(
            MetodoPago metodo,
            @DecimalMin(value = "0.00", message = "El monto en efectivo no puede ser negativo") BigDecimal montoEfectivo,
            @DecimalMin(value = "0.00", message = "El monto QR no puede ser negativo") BigDecimal montoQr,
            @DecimalMin(value = "0.00", message = "El monto recibido no puede ser negativo") BigDecimal totalRecibido,
            @DecimalMin(value = "0.00", message = "El cambio no puede ser negativo") BigDecimal cambio,
            @Size(max = 100, message = "La referencia QR no puede superar 100 caracteres") String referenciaQr,
            BigDecimal pagoInicial,
            LocalDate fechaLimite,
            @Size(max = 500, message = "La observacion no puede superar 500 caracteres") String observacion) {
    }
}
