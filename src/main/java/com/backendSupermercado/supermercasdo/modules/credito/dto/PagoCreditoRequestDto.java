package com.backendSupermercado.supermercasdo.modules.credito.dto;

import java.math.BigDecimal;
import com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PagoCreditoRequestDto(
        @NotNull Long idSesionCaja,
        @NotNull @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero") BigDecimal monto,
        @NotNull MetodoPago metodo,
        BigDecimal montoEfectivo,
        BigDecimal montoQr,
        @Size(max = 100) String referenciaQr) {}
