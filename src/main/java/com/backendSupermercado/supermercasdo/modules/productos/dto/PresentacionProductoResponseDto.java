package com.backendSupermercado.supermercasdo.modules.productos.dto;

import java.math.BigDecimal;
import java.util.List;

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
public class PresentacionProductoResponseDto {

    private Long idPresentacion;
    private String nombrePresentacion;
    private BigDecimal factorConversion;
    private BigDecimal precioVenta;
    private Boolean esPrincipal;
    private Boolean estado;
    private Long idUnidadMedida;
    private String nombreUnidadMedida;
    private List<CodigoBarraResponseDto> codigosBarra;
}