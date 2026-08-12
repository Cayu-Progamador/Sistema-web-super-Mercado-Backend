package com.backendSupermercado.supermercasdo.modules.productos.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresentacionProductoRequestDto {

    @NotBlank(message = "El nombre de la presentación es obligatorio")
    @Size(max = 100, message = "El nombre de la presentación no puede superar 100 caracteres")
    private String nombrePresentacion;

    @NotNull(message = "La unidad de medida de la presentación es obligatoria")
    private Long idUnidadMedida;

    @NotNull(message = "El factor de conversión es obligatorio")
    @DecimalMin(value = "0.0001", message = "El factor de conversión debe ser mayor a cero")
    private BigDecimal factorConversion;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a cero")
    private BigDecimal precioVenta;

    @Builder.Default
    private Boolean esPrincipal = false;

    @Builder.Default
    private Boolean usaCodigo = false;

    private String codigo;

    @Valid
    private List<CodigoBarraRequestDto> codigos;
}
