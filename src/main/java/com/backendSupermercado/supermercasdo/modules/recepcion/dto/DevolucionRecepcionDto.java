package com.backendSupermercado.supermercasdo.modules.recepcion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DevolucionRecepcionDto {

    private Long idDevolucion;
    private Long idRecepcion;
    private Long idPedido;
    private String numeroRecepcion;
    private String proveedor;
    private LocalDateTime fechaDevolucion;
    private String estado;
    private String observacion;
    private List<ItemDto> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {
        private Long idDetallePedido;
        private String producto;
        private BigDecimal cantidad;
        private BigDecimal cantidadReemplazada;
        private BigDecimal pendienteReemplazo;
        private String motivo;
    }
}
