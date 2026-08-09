package com.backendSupermercado.supermercasdo.mapper.productos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.productos.dto.CodigoBarraResponseDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.PresentacionProductoResponseDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoResponseDto;
import com.backendSupermercado.supermercasdo.modules.productos.entity.CodigoBarra;
import com.backendSupermercado.supermercasdo.modules.productos.entity.PresentacionProducto;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;

@Component
public class ProductoMapper {

    public ProductoResponseDto toResponseDto(Producto producto) {
        ProductoResponseDto dto = ProductoResponseDto.builder()
                .idProducto(producto.getIdProducto())
                .codigoInterno(producto.getCodigoInterno())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .controlaLote(producto.getControlaLote())
                .controlaVencimiento(producto.getControlaVencimiento())
                .imagen(producto.getImagen())
                .estado(producto.getEstado())
                .fechaCreacion(producto.getFechaCreacion())
                .fechaActualizacion(producto.getFechaActualizacion())
                .idSubcategoria(producto.getSubcategoria() != null
                        ? producto.getSubcategoria().getIdSubcategoria() : null)
                .nombreSubcategoria(producto.getSubcategoria() != null
                        ? producto.getSubcategoria().getNombre() : null)
                .idCategoria(producto.getSubcategoria() != null
                        && producto.getSubcategoria().getCategoria() != null
                                ? producto.getSubcategoria().getCategoria().getIdCategoria() : null)
                .nombreCategoria(producto.getSubcategoria() != null
                        && producto.getSubcategoria().getCategoria() != null
                                ? producto.getSubcategoria().getCategoria().getNombre() : null)
                .idMarca(producto.getMarca() != null ? producto.getMarca().getIdMarca() : null)
                .nombreMarca(producto.getMarca() != null ? producto.getMarca().getNombre() : null)
                .idUnidadBase(producto.getUnidadBase() != null
                        ? producto.getUnidadBase().getIdUnidadMedida() : null)
                .nombreUnidadBase(producto.getUnidadBase() != null
                        ? producto.getUnidadBase().getNombre() : null)
                .abreviaturaUnidadBase(producto.getUnidadBase() != null
                        ? producto.getUnidadBase().getAbreviatura() : null)
                .build();

        if (producto.getPresentaciones() != null) {
            dto.setPresentaciones(toPresentacionesDto(producto.getPresentaciones()));
        }
        return dto;
    }

    public List<PresentacionProductoResponseDto> toPresentacionesDto(
            List<PresentacionProducto> presentaciones) {
        return presentaciones.stream()
                .map(this::toPresentacionDto)
                .collect(Collectors.toList());
    }

    public PresentacionProductoResponseDto toPresentacionDto(PresentacionProducto presentacion) {
        PresentacionProductoResponseDto dto = PresentacionProductoResponseDto.builder()
                .idPresentacion(presentacion.getIdPresentacion())
                .nombrePresentacion(presentacion.getNombrePresentacion())
                .factorConversion(presentacion.getFactorConversion())
                .precioVenta(presentacion.getPrecioVenta())
                .esPrincipal(presentacion.getEsPrincipal())
                .estado(presentacion.getEstado())
                .idUnidadMedida(presentacion.getUnidadMedida() != null
                        ? presentacion.getUnidadMedida().getIdUnidadMedida() : null)
                .nombreUnidadMedida(presentacion.getUnidadMedida() != null
                        ? presentacion.getUnidadMedida().getNombre() : null)
                .build();

        if (presentacion.getCodigosBarra() != null) {
            dto.setCodigosBarra(presentacion.getCodigosBarra().stream()
                    .map(this::toCodigoBarraDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public CodigoBarraResponseDto toCodigoBarraDto(CodigoBarra codigoBarra) {
        return CodigoBarraResponseDto.builder()
                .idCodigoBarra(codigoBarra.getIdCodigoBarra())
                .codigo(codigoBarra.getCodigo())
                .principal(codigoBarra.getPrincipal())
                .estado(codigoBarra.getEstado())
                .idPresentacion(codigoBarra.getPresentacionProducto() != null
                        ? codigoBarra.getPresentacionProducto().getIdPresentacion() : null)
                .build();
    }
}