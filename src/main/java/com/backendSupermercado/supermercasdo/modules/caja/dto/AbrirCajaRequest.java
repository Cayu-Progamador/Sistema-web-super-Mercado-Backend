package com.backendSupermercado.supermercasdo.modules.caja.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record AbrirCajaRequest(@NotNull Long idCaja, @NotNull @DecimalMin("0.00") BigDecimal montoInicial,
        String observacion) {
}
