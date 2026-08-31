package com.backendSupermercado.supermercasdo.modules.devoluciones.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevolucionProveedorResponseDto {
    private Long idDevolucion;
    private String numero;
    private Long idRecepcion;
    private String numeroRecepcion;
    private String proveedor;
    private Long idUsuario;
    private String usuario;
    private LocalDateTime fechaDevolucion;
    private String estado;
    private String observacion;
    private BigDecimal cantidadTotal;
    private List<DetalleDto> detalles = new ArrayList<>();

    @Getter
    @Setter
    public static class DetalleDto {
        private Long idDetalleDevolucion;
        private Long idDetalleRecepcion;
        private Long idProducto;
        private String producto;
        private Long idLote;
        private String lote;
        private BigDecimal cantidad;
        private Long idMotivoDevolucion;
        private String motivo;
        private String observacion;
        private List<OrigenDto> origenes = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class OrigenDto {
        private Long idDetalleOrigen;
        private Long idUbicacion;
        private String ubicacion;
        private String almacen;
        private BigDecimal cantidad;
    }
}
