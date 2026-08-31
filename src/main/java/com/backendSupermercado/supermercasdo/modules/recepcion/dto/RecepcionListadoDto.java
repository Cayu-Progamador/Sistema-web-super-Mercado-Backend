package com.backendSupermercado.supermercasdo.modules.recepcion.dto;

import java.math.BigDecimal;
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
public class RecepcionListadoDto {

    private Long idRecepcion;
    private String numero;
    private Long idPedido;
    private String numeroPedido;
    private Long idProveedor;
    private String proveedor;
    private LocalDateTime fechaRecepcion;
    private String usuario;
    private String estado;
    private String distribucion;
    private String observacion;
    private BigDecimal totalCantidad;
    private BigDecimal totalGeneral;
    private List<RecepcionDetalleListadoDto> detalle = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecepcionDetalleListadoDto {
        private Long idDetalleRecepcion;
        private Long idProducto;
        private String producto;
        private BigDecimal solicitado;
        private BigDecimal recibido;
        private BigDecimal aceptado;
        private BigDecimal rechazado;
        private BigDecimal distribuido;
        private String motivo;
        private BigDecimal precio;
        private BigDecimal subtotal;
        private String lote;
        private String vencimiento;
    }
}