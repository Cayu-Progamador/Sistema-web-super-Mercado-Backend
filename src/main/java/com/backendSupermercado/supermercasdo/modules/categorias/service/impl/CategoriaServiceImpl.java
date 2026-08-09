package com.backendSupermercado.supermercasdo.modules.categorias.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.CategoriaDetalleResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.CategoriaRequestDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.CategoriaResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.dto.SubcategoriaDetalleResponseDto;
import com.backendSupermercado.supermercasdo.modules.categorias.entity.Categoria;
import com.backendSupermercado.supermercasdo.modules.categorias.repository.CategoriaRepository;
import com.backendSupermercado.supermercasdo.modules.categorias.repository.SubcategoriaRepository;
import com.backendSupermercado.supermercasdo.modules.categorias.service.CategoriaService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final SubcategoriaRepository subcategoriaRepository;

    @Override
    @Transactional
    public CategoriaResponseDto crearCategoria(CategoriaRequestDto dto) {
        String nombre = dto.getNombre().trim();

        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResourceConflictException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = Categoria.builder()
                .nombre(nombre)
                .descripcion(trimDescripcion(dto.getDescripcion()))
                .estado(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return toResponseDto(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaResponseDto actualizarCategoria(Long idCategoria, CategoriaRequestDto dto) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        String nombre = dto.getNombre().trim();

        if (categoriaRepository.existsByNombreIgnoreCase(nombre)
                && !categoria.getNombre().equalsIgnoreCase(nombre)) {
            throw new ResourceConflictException("Ya existe una categoría con ese nombre");
        }

        categoria.setNombre(nombre);
        categoria.setDescripcion(trimDescripcion(dto.getDescripcion()));
        categoria.setFechaActualizacion(LocalDateTime.now());
        return toResponseDto(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDetalleResponseDto obtenerDetalleCategoria(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        List<SubcategoriaDetalleResponseDto> subcategorias = categoria.getSubcategorias().stream()
                .map(s -> SubcategoriaDetalleResponseDto.builder()
                        .idSubcategoria(s.getIdSubcategoria())
                        .nombre(s.getNombre())
                        .descripcion(s.getDescripcion())
                        .estado(s.getEstado())
                        .cantidadProductos(s.getProductos() != null ? (long) s.getProductos().size() : 0L)
                        .build())
                .toList();

        return CategoriaDetalleResponseDto.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .estado(categoria.getEstado())
                .fechaCreacion(categoria.getFechaCreacion())
                .fechaActualizacion(categoria.getFechaActualizacion())
                .subcategorias(subcategorias)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> obtenerResumenCategorias() {
        return Map.of(
                "totalCategorias", categoriaRepository.count(),
                "categoriasActivas", categoriaRepository.countByEstadoTrue(),
                "categoriasInactivas", categoriaRepository.countByEstadoFalse(),
                "subcategorias", subcategoriaRepository.count());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDto> listarCategorias(String busqueda) {
        List<Categoria> categorias = (busqueda == null || busqueda.isBlank())
                ? categoriaRepository.findAll()
                : categoriaRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(busqueda.trim());

        return categorias.stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDto> listarCategoriasActivas() {
        return categoriaRepository.findByEstadoTrueOrderByNombreAsc()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaResponseDto> listarCategoriasPaginado(String busqueda, Boolean estado, Pageable pageable) {
        String patron = (busqueda == null || busqueda.isBlank())
                ? null
                : "%" + busqueda.trim().toLowerCase(Locale.ROOT) + "%";
        return categoriaRepository.filtrar(patron, estado, pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public CategoriaResponseDto activarCategoria(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        if (categoria.getEstado()) {
            throw new ResourceConflictException("La categoría ya está activada");
        }

        categoria.setEstado(true);
        categoria.setFechaActualizacion(LocalDateTime.now());
        return toResponseDto(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaResponseDto desactivarCategoria(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));

        if (!categoria.getEstado()) {
            throw new ResourceConflictException("La categoría ya está desactivada");
        }

        categoria.setEstado(false);
        categoria.setFechaActualizacion(LocalDateTime.now());
        return toResponseDto(categoriaRepository.save(categoria));
    }

    private String trimDescripcion(String descripcion) {
        return (descripcion == null || descripcion.isBlank()) ? null : descripcion.trim();
    }

    private CategoriaResponseDto toResponseDto(Categoria categoria) {
        return CategoriaResponseDto.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .estado(categoria.getEstado())
                .fechaCreacion(categoria.getFechaCreacion())
                .fechaActualizacion(categoria.getFechaActualizacion())
                .build();
    }
}
