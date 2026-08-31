package com.backendSupermercado.supermercasdo.modules.pedidos.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoListadoDto {

    private Long idPedido;
    private String numero;
    private Long idProveedor;
    private String proveedor;
    private Long idUsuario;
    private String usuario;
    private LocalDateTime fechaPedido;
    private LocalDate fechaEntregaEstimada;
    private String estado;
    private String observacion;
    private List<PedidoDetalleListadoDto> detalles = new ArrayList<>();
}