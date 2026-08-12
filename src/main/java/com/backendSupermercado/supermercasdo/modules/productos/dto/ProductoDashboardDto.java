package com.backendSupermercado.supermercasdo.modules.productos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductoDashboardDto {
    private long total;
    private long activos;
    private long inactivos;
    private long conPresentaciones;
    private long categorias;
    private long marcas;
}