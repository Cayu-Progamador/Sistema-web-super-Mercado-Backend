package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelarInventarioFisicoRequest(
        @NotBlank @Size(max = 500) String motivo) {
}
