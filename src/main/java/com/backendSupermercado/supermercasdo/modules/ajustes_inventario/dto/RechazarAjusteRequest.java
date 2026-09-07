package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RechazarAjusteRequest(
        @NotBlank(message = "El motivo de rechazo es obligatorio")
        @Size(max = 500, message = "El motivo de rechazo no puede superar 500 caracteres")
        String motivo) {
}
