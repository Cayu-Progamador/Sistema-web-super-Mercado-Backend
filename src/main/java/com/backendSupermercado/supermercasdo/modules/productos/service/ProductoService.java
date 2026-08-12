package com.backendSupermercado.supermercasdo.modules.productos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoDashboardDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoRequestDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoResponseDto;

public interface ProductoService {

    ProductoDashboardDto obtenerDashboard();

    ProductoResponseDto crearProducto(ProductoRequestDto dto);

    ProductoResponseDto actualizarProducto(Long id, ProductoRequestDto dto);

    ProductoResponseDto activarProducto(Long id);

    ProductoResponseDto desactivarProducto(Long id);

    Page<ProductoResponseDto> listarProductosPaginado(
            String busqueda,
            Boolean estado,
            Long idCategoria,
            Long idSubcategoria,
            Long idMarca,
            Long idUnidadBase,
            Boolean tienePresentaciones,
            String codigoBarra,
            Pageable pageable);

    List<ProductoResponseDto> exportarProductos(
            String busqueda,
            Boolean estado,
            Long idCategoria,
            Long idSubcategoria,
            Long idMarca,
            Long idUnidadBase,
            String codigoBarra);

    byte[] exportarProductosPDF(
            String busqueda,
            Boolean estado,
            Long idCategoria,
            Long idSubcategoria,
            Long idMarca,
            Long idUnidadBase,
            String codigoBarra,
            String username);

    byte[] exportarProductosExcel(
            String busqueda,
            Boolean estado,
            Long idCategoria,
            Long idSubcategoria,
            Long idMarca,
            Long idUnidadBase,
            String codigoBarra);
}
