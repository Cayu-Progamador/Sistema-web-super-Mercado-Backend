package com.backendSupermercado.supermercasdo.modules.caja.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record CerrarCajaRequest(@NotNull @DecimalMin("0.00") BigDecimal efectivoContado, String observacion) {
}
