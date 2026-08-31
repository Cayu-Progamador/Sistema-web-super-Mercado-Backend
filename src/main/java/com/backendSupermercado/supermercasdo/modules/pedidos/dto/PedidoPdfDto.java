package com.backendSupermercado.supermercasdo.modules.pedidos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class PedidoPdfDto {

    private EmpresaDto empresa;
    private ProveedorPdfDto proveedor;
    private PedidoPdfInnerDto pedido;
    private List<DetallePdfDto> detalles;
    private String generadoPor;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmpresaDto {
        private String nombre;
        private String nit;
        private String direccion;
        private String telefono;
        private String ciudad;
        private String logoPath;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProveedorPdfDto {
        private String nombre;
        private String documento;
        private String direccion;
        private String telefono;
        private String correo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PedidoPdfInnerDto {
        private String numero;
        private LocalDateTime fechaPedido;
        private LocalDate fechaEntregaEstimada;
        private String estado;
        private String usuario;
        private String observacion;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetallePdfDto {
        private String producto;
        private String unidad;
        private BigDecimal cantidad;
        private BigDecimal factor;
        private String equivalencia;
    }
}