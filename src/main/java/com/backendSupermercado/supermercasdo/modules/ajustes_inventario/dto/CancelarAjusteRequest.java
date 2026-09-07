package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelarAjusteRequest(
        @NotBlank(message = "El motivo de cancelacion es obligatorio")
        @Size(max = 500, message = "El motivo no puede superar 500 caracteres")
        String motivo) {
}
