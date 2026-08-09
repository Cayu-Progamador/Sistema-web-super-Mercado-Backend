package com.backendSupermercado.supermercasdo.modules.productos.dto;


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
public class CodigoBarraResponseDto {

    private Long idCodigoBarra;
    private String codigo;
    private Boolean principal;
    private Boolean estado;
    private Long idPresentacion;
}