package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record GuardarDiferenciasRequest(
        @NotNull @Size(min = 1) @Valid List<ItemDiferencia> detalles) {

    public record ItemDiferencia(
            @NotNull Long idDetalle,
            @NotBlank @Size(max = 100) String motivo,
            @Size(max = 500) String observacion) {
    }
}
