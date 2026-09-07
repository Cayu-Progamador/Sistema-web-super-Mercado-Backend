package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.MotivoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.TipoAjusteInventario;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearAjusteInventarioRequest(
        @NotNull(message = "El tipo de ajuste es obligatorio")
        TipoAjusteInventario tipo,
        @NotNull(message = "El motivo del ajuste es obligatorio")
        MotivoAjusteInventario motivo,
        @Size(max = 500, message = "La observacion no puede superar 500 caracteres")
        String observacion,
        LocalDate fechaLevantamiento,
        @Size(max = 150, message = "El responsable no puede superar 150 caracteres")
        String responsableLevantamiento,
        @Size(max = 200, message = "El lugar del hallazgo no puede superar 200 caracteres")
        String lugarHallazgo,
        LocalDate fechaHallazgo,
        @Size(max = 150, message = "La persona que encontro el producto no puede superar 150 caracteres")
        String encontradoPor,
        @NotEmpty(message = "Debe agregar al menos un producto")
        List<@Valid DetalleRequest> detalles) {

    public record DetalleRequest(
            @NotNull(message = "El producto es obligatorio") Long idProducto,
            Long idPresentacion,
            @DecimalMin(value = "0.001", message = "La cantidad de la presentacion debe ser mayor que cero")
            BigDecimal cantidadPresentacion,
            Long idLote,
            @Size(max = 50, message = "El codigo de lote no puede superar 50 caracteres") String codigoLote,
            LocalDate fechaFabricacion,
            LocalDate fechaVencimiento,
            @NotNull(message = "La ubicacion es obligatoria") Long idUbicacion,
            @NotNull(message = "La cantidad es obligatoria")
            @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor que cero")
            BigDecimal cantidad,
            @DecimalMin(value = "0.01", message = "El costo unitario debe ser mayor que cero")
            BigDecimal costoUnitario,
            @Size(max = 40, message = "El tipo de merma no puede superar 40 caracteres") String tipoMerma,
            @Size(max = 500, message = "La causa de merma no puede superar 500 caracteres") String causaMerma) {
    }
}
