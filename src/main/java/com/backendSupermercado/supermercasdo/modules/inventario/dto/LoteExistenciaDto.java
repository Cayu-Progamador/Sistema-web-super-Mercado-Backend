package com.backendSupermercado.supermercasdo.modules.inventario.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
public record LoteExistenciaDto(Long idLote, String codigoLote, BigDecimal cantidad, String almacen, String ubicacion, LocalDate fechaVencimiento) { }
