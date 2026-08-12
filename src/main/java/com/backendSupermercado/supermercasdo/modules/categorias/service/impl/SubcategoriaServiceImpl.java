package com.backendSupermercado.supermercasdo.modules.categorias.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaConProductosResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaProductoResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaRequestDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.entity.Categoria;
import com.backendSupermercado.supermercasdo.modules.categorias.entity.Subcategoria;
import com.backendSupermercado.supermercasdo.modules.categorias.repository.CategoriaRepository;
import com.backendSupermercado.supermercasdo.modules.categorias.repository.SubcategoriaRepository;
import com.backendSupermercado.supermercasdo.modules.categorias.service.SubcategoriaService;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
import com.backendSupermercado.supermercasdo.modules.productos.repository.ProductoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubcategoriaServiceImpl implements SubcategoriaService {

    private final SubcategoriaRepository subcategoriaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public SubcategoriaResponseDto crearSubcategoria(SubcategoriaRequestDto dto) {
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new ResourceConflictException("Categoría no encontrada"));

        if (!categoria.getEstado()) {
            throw new ResourceConflictException("No se puede crear una subcategoría en una categoría desactivada");
        }

        String nombre = dto.getNombre().trim();

        if (subcategoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResourceConflictException("Ya existe una subcategoría con ese nombre");
        }

        Subcategoria subcategoria = Subcategoria.builder()
                .nombre(nombre)
                .descripcion(trimDescripcion(dto.getDescripcion()))
                .estado(true)
                .fechaCreacion(LocalDateTime.now())
                .categoria(categoria)
                .build();

        return toResponseDto(subcategoriaRepository.save(subcategoria));
    }

    @Override
    @Transactional
    public SubcategoriaResponseDto actualizarSubcategoria(Long idSubcategoria, SubcategoriaRequestDto dto) {
        Subcategoria subcategoria = subcategoriaRepository.findById(idSubcategoria)
                .orElseThrow(() -> new EntityNotFoundException("Subcategoría no encontrada"));

        String nombre = dto.getNombre().trim();

        if (subcategoriaRepository.existsByNombreIgnoreCase(nombre)
                && !subcategoria.getNombre().equalsIgnoreCase(nombre)) {
            throw new ResourceConflictException("Ya existe una subcategoría con ese nombre");
        }

        subcategoria.setNombre(nombre);
        subcategoria.setDescripcion(trimDescripcion(dto.getDescripcion()));
        subcategoria.setFechaActualizacion(LocalDateTime.now());
        return toResponseDto(subcategoriaRepository.save(subcategoria));
    }

    @Override
    @Transactional(readOnly = true)
    public SubcategoriaConProductosResponseDto obtenerDetalleSubcategoria(Long idSubcategoria) {
        Subcategoria subcategoria = subcategoriaRepository.findById(idSubcategoria)
                .orElseThrow(() -> new EntityNotFoundException("Subcategoría no encontrada"));

        List<SubcategoriaProductoResponseDto> productos = productoRepository
                .findBySubcategoriaIdSubcategoriaOrderByNombreAsc(idSubcategoria)
                .stream()
                .map(this::toProductoDto)
                .toList();

        Categoria categoria = subcategoria.getCategoria();

        return SubcategoriaConProductosResponseDto.builder()
                .idSubcategoria(subcategoria.getIdSubcategoria())
                .nombre(subcategoria.getNombre())
                .descripcion(subcategoria.getDescripcion())
                .estado(subcategoria.getEstado())
                .fechaCreacion(subcategoria.getFechaCreacion())
                .fechaActualizacion(subcategoria.getFechaActualizacion())
                .idCategoria(categoria != null ? categoria.getIdCategoria() : null)
                .nombreCategoria(categoria != null ? categoria.getNombre() : null)
                .productos(productos)
                .build();
    }

    private SubcategoriaProductoResponseDto toProductoDto(Producto producto) {
        return SubcategoriaProductoResponseDto.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .imagen(producto.getImagen())
                .estado(producto.getEstado())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubcategoriaResponseDto> listarSubcategoriasPorCategoria(Long idCategoria) {
        return subcategoriaRepository.findByCategoriaActivaOrderByNombreAsc(idCategoria)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    private String trimDescripcion(String descripcion) {
        return (descripcion == null || descripcion.isBlank()) ? null : descripcion.trim();
    }

    private SubcategoriaResponseDto toResponseDto(Subcategoria subcategoria) {
        Categoria categoria = subcategoria.getCategoria();
        return SubcategoriaResponseDto.builder()
                .idSubcategoria(subcategoria.getIdSubcategoria())
                .nombre(subcategoria.getNombre())
                .descripcion(subcategoria.getDescripcion())
                .estado(subcategoria.getEstado())
                .idCategoria(categoria != null ? categoria.getIdCategoria() : null)
                .nombreCategoria(categoria != null ? categoria.getNombre() : null)
                .cantidadProductos(subcategoria.getProductos() != null
                        ? (long) subcategoria.getProductos().size()
                        : 0L)
                .fechaCreacion(subcategoria.getFechaCreacion())
                .fechaActualizacion(subcategoria.getFechaActualizacion())
                .build();
    }
}
