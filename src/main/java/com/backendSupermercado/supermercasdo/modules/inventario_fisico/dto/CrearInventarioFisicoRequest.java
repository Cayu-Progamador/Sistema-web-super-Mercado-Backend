package com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto;

import java.time.LocalDate;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.TipoInventarioFisico;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearInventarioFisicoRequest(
        @NotNull(message = "El tipo de inventario es obligatorio") TipoInventarioFisico tipo,
        Long idAlmacen,
        Long idUbicacion,
        Long idCategoria,
        List<Long> idsProductos,
        @NotNull(message = "El responsable es obligatorio") Long idResponsable,
        @NotNull(message = "La fecha programada es obligatoria")
        @FutureOrPresent(message = "La fecha programada no puede estar en el pasado") LocalDate fechaProgramada,
        @NotNull(message = "Debe indicar si el conteo es ciego") Boolean conteoCiego,
        @Size(max = 500, message = "La observación no puede superar 500 caracteres") String observacion) {
}
