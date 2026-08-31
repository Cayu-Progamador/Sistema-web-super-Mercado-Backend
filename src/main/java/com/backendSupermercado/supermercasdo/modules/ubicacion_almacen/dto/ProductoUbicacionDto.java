package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter @AllArgsConstructor
public class ProductoUbicacionDto { private Long idProducto; private Long idLote; private String producto; private String lote; private BigDecimal cantidad; private String unidad; private LocalDate vencimiento; private String estado; }
