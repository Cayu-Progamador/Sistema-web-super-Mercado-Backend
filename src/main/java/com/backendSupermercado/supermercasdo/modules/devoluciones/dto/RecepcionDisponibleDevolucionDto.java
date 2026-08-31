package com.backendSupermercado.supermercasdo.modules.devoluciones.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecepcionDisponibleDevolucionDto {
    private Long idRecepcion;
    private String numero;
    private String proveedor;
    private LocalDateTime fechaRecepcion;
    private List<DetalleDisponibleDto> detalles = new ArrayList<>();

    @Getter
    @Setter
    public static class DetalleDisponibleDto {
        private Long idDetalleRecepcion;
        private Long idProducto;
        private String producto;
        private Long idLote;
        private String lote;
        private BigDecimal recibido;
        private BigDecimal yaDevuelto;
        private BigDecimal disponibleParaDevolver;
        private List<UbicacionDisponibleDto> ubicaciones = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class UbicacionDisponibleDto {
        private Long idUbicacion;
        private String codigo;
        private String nombre;
        private Long idAlmacen;
        private String almacen;
        private String tipoAlmacen;
        private BigDecimal disponible;
    }
}
