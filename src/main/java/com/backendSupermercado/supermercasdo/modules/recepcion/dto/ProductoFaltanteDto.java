package com.backendSupermercado.supermercasdo.modules.recepcion.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoFaltanteDto {

    private Long idPedido;
    private Long idDetallePedido;
    private String numeroPedido;
    private String proveedor;
    private String producto;
    private String unidad;
    private BigDecimal solicitado;
    private BigDecimal recibido;
    private BigDecimal faltante;
}
