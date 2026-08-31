package com.backendSupermercado.supermercasdo.modules.pedidos.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalleListadoDto {

    private Long idDetalle;
    private Long idProducto;
    private String producto;
    private Long idUnidadMedida;
    private String unidad;
    private BigDecimal cantidad;
    private BigDecimal factor;
    private String equivalencia;
    private Boolean controlaLote;
    private Boolean controlaVencimiento;
    private String unidadBase;
    private BigDecimal precio;
}