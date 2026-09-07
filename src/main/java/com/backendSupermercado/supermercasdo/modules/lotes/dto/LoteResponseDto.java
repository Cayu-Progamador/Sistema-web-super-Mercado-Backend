package com.backendSupermercado.supermercasdo.modules.lotes.dto;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime;
public record LoteResponseDto(Long idLote,String codigoLote,Long idProducto,String producto,LocalDateTime fechaIngreso,LocalDate fechaFabricacion,LocalDate fechaVencimiento,BigDecimal cantidad,Boolean activo,String estado,String almacen,String ubicacion,BigDecimal costoUnitario){ }
