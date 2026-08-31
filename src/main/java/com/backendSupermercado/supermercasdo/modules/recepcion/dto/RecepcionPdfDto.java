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
public class RecepcionPdfDto {

    private EmpresaDto empresa;
    private ProveedorPdfDto proveedor;
    private RecepcionInnerDto recepcion;
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
    public static class RecepcionInnerDto {
        private String numero;
        private String numeroPedido;
        private LocalDateTime fechaRecepcion;
        private String usuario;
        private String estado;
        private String distribucion;
        private String observacion;
        private String usuarioAnulacion;
        private LocalDateTime fechaAnulacion;
        private BigDecimal totalGeneral;
        private Integer totalProductos;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetallePdfDto {
        private String producto;
        private String lote;
        private String vencimiento;
        private BigDecimal solicitado;
        private BigDecimal recibido;
        private BigDecimal aceptado;
        private BigDecimal rechazado;
        private String motivo;
        private BigDecimal distribuido;
        private BigDecimal precio;
        private BigDecimal subtotal;
    }
}