package com.backendSupermercado.supermercasdo.modules.productos.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.mapper.productos.ProductoMapper;
import com.backendSupermercado.supermercasdo.modules.categorias.entity.Subcategoria;
import com.backendSupermercado.supermercasdo.modules.categorias.repository.CategoriaRepository;
import com.backendSupermercado.supermercasdo.modules.categorias.repository.SubcategoriaRepository;
import com.backendSupermercado.supermercasdo.modules.marcas.entity.Marca;
import com.backendSupermercado.supermercasdo.modules.marcas.repository.MarcaRepository;
import com.backendSupermercado.supermercasdo.modules.productos.dto.CodigoBarraRequestDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.PresentacionProductoRequestDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoDashboardDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoRequestDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoResponseDto;
import com.backendSupermercado.supermercasdo.modules.productos.entity.CodigoBarra;
import com.backendSupermercado.supermercasdo.modules.productos.entity.PresentacionProducto;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
import com.backendSupermercado.supermercasdo.modules.productos.repository.CodigoBarraRepository;
import com.backendSupermercado.supermercasdo.modules.productos.repository.ProductoRepository;
import com.backendSupermercado.supermercasdo.modules.productos.service.ProductoService;
import com.backendSupermercado.supermercasdo.modules.inventario.entity.ConfiguracionStock;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ConfiguracionStockRepository;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.UnidadMedida;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.repository.UnidadMedidaRepository;
import com.backendSupermercado.supermercasdo.shared.util.ReporteProductoUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final CodigoBarraRepository codigoBarraRepository;
    private final ProductoMapper productoMapper;
    private final ConfiguracionStockRepository configuracionStockRepository;

    @Override
    @Transactional(readOnly = true)
    public ProductoDashboardDto obtenerDashboard() {
        return new ProductoDashboardDto(
                productoRepository.count(),
                productoRepository.countByEstadoTrue(),
                productoRepository.countByEstadoFalse(),
                productoRepository.countConPresentaciones(),
                categoriaRepository.count(),
                marcaRepository.count());
    }

    @Override
    @Transactional
    public ProductoResponseDto crearProducto(ProductoRequestDto dto) {
        String nombre = dto.getNombre().trim();

        validarSubcategoria(dto.getIdSubcategoria());
        Marca marca = validarMarca(dto.getIdMarca());
        validarUnidadBase(dto.getIdUnidadBase());
        validarNombreDuplicado(nombre, dto.getIdMarca(), dto.getIdSubcategoria());
        String codigoInterno = generarCodigoInterno(dto.getCodigoInterno());

        Producto producto = Producto.builder()
                .codigoInterno(codigoInterno)
                .nombre(nombre)
                .descripcion(trimDescripcion(dto.getDescripcion()))
                .controlaLote(dto.getControlaLote() != null && dto.getControlaLote())
                .controlaVencimiento(dto.getControlaVencimiento() != null && dto.getControlaVencimiento())
                .imagen(dto.getImagen())
                .estado(true)
                .fechaCreacion(LocalDateTime.now())
                .subcategoria(subcategoriaRepository.getReferenceById(dto.getIdSubcategoria()))
                .marca(marca)
                .unidadBase(unidadMedidaRepository.getReferenceById(dto.getIdUnidadBase()))
                .build();

        producto.setPresentaciones(mapearPresentaciones(dto.getPresentaciones(), producto));

        try {
            Producto guardado = productoRepository.save(producto);
            guardarConfiguracionStock(guardado, dto);
            return productoMapper.toResponseDto(guardado);
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException(
                    "No se pudo guardar el producto. Verifica que el código interno y los códigos de barras sean únicos.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDto> listarProductosPaginado(
            String busqueda,
            Boolean estado,
            Long idCategoria,
            Long idSubcategoria,
            Long idMarca,
            Long idUnidadBase,
            Boolean tienePresentaciones,
            String codigoBarra,
            Pageable pageable) {
        String patron = (busqueda == null || busqueda.isBlank())
                ? null
                : "%" + busqueda.trim().toLowerCase(Locale.ROOT) + "%";
        String codigo = (codigoBarra == null || codigoBarra.isBlank())
                ? null
                : codigoBarra.trim();

        return productoRepository.filtrar(patron, estado, idCategoria, idSubcategoria,
                idMarca, idUnidadBase, tienePresentaciones, codigo, pageable)
                .map(productoMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDto> exportarProductos(
            String busqueda,
            Boolean estado,
            Long idCategoria,
            Long idSubcategoria,
            Long idMarca,
            Long idUnidadBase,
            String codigoBarra) {
        String patron = (busqueda == null || busqueda.isBlank())
                ? null
                : "%" + busqueda.trim().toLowerCase(Locale.ROOT) + "%";
        String codigo = (codigoBarra == null || codigoBarra.isBlank())
                ? null
                : codigoBarra.trim();

        return productoRepository.filtrar(patron, estado, idCategoria, idSubcategoria,
                idMarca, idUnidadBase, null, codigo, Pageable.unpaged())
                .map(productoMapper::toResponseDto)
                .stream()
                .toList();
    }

    @Override
    public byte[] exportarProductosPDF(
            String busqueda,
            Boolean estado,
            Long idCategoria,
            Long idSubcategoria,
            Long idMarca,
            Long idUnidadBase,
            String codigoBarra,
            String username) {
        List<ProductoResponseDto> data = exportarProductos(busqueda, estado, idCategoria,
                idSubcategoria, idMarca, idUnidadBase, codigoBarra);
        return ReporteProductoUtil.generarPdf(data, username);
    }

    @Override
    public byte[] exportarProductosExcel(
            String busqueda,
            Boolean estado,
            Long idCategoria,
            Long idSubcategoria,
            Long idMarca,
            Long idUnidadBase,
            String codigoBarra) {
        List<ProductoResponseDto> data = exportarProductos(busqueda, estado, idCategoria,
                idSubcategoria, idMarca, idUnidadBase, codigoBarra);
        return ReporteProductoUtil.generarExcel(data);
    }

    private void validarSubcategoria(Long idSubcategoria) {
        Subcategoria subcategoria = subcategoriaRepository.findById(idSubcategoria)
                .orElseThrow(() -> new EntityNotFoundException("Subcategoría no encontrada"));

        if (!subcategoria.getEstado()) {
            throw new ResourceConflictException("No se puede crear un producto en una subcategoría desactivada");
        }
        if (subcategoria.getCategoria() != null && !subcategoria.getCategoria().getEstado()) {
            throw new ResourceConflictException("No se puede crear un producto en una categoría desactivada");
        }
    }

    private Marca validarMarca(Long idMarca) {
        if (idMarca == null) {
            return null;
        }
        Marca marca = marcaRepository.findById(idMarca)
                .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada"));
        if (!marca.getEstado()) {
            throw new ResourceConflictException("No se puede usar una marca desactivada");
        }
        return marca;
    }

    private void validarUnidadBase(Long idUnidadBase) {
        UnidadMedida unidad = unidadMedidaRepository.findById(idUnidadBase)
                .orElseThrow(() -> new EntityNotFoundException("Unidad de medida no encontrada"));
        if (!unidad.getEstado()) {
            throw new ResourceConflictException("No se puede usar una unidad de medida desactivada");
        }
    }

    private void validarNombreDuplicado(String nombre, Long idMarca, Long idSubcategoria) {
        boolean existe = (idMarca == null)
                ? productoRepository.existsByNombreIgnoreCaseAndSubcategoria_IdSubcategoria(
                        nombre, idSubcategoria)
                : productoRepository.existsByNombreIgnoreCaseAndMarca_IdMarcaAndSubcategoria_IdSubcategoria(
                        nombre, idMarca, idSubcategoria);

        if (existe) {
            throw new ResourceConflictException("Ya existe un producto con ese nombre en esa subcategoría");
        }
    }

    private String generarCodigoInterno(String codigoIngresado) {
        String codigo = (codigoIngresado == null || codigoIngresado.isBlank())
                ? null
                : codigoIngresado.trim();

        if (codigo == null) {
            codigo = generarCodigoSecuencial();
        }

        if (productoRepository.existsByCodigoInterno(codigo)) {
            throw new ResourceConflictException("Ya existe un producto con el código interno: " + codigo);
        }
        return codigo;
    }

    private String generarCodigoSecuencial() {
        long siguiente = productoRepository.count() + 1;
        String candidato = String.format("PRO-%06d", siguiente);
        while (productoRepository.existsByCodigoInterno(candidato)) {
            siguiente++;
            candidato = String.format("PRO-%06d", siguiente);
        }
        return candidato;
    }

    @Override
    @Transactional
    public ProductoResponseDto actualizarProducto(Long id, ProductoRequestDto dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        String nombre = dto.getNombre().trim();

        validarSubcategoria(dto.getIdSubcategoria());
        Marca marca = validarMarca(dto.getIdMarca());
        validarUnidadBase(dto.getIdUnidadBase());
        validarNombreDuplicadoEnEdicion(nombre, dto.getIdMarca(), dto.getIdSubcategoria(), id);

        producto.setNombre(nombre);
        producto.setDescripcion(trimDescripcion(dto.getDescripcion()));
        producto.setControlaLote(dto.getControlaLote() != null && dto.getControlaLote());
        producto.setControlaVencimiento(dto.getControlaVencimiento() != null && dto.getControlaVencimiento());
        producto.setImagen(trimDescripcion(dto.getImagen()));
        producto.setSubcategoria(subcategoriaRepository.getReferenceById(dto.getIdSubcategoria()));
        producto.setMarca(marca);
        producto.setUnidadBase(unidadMedidaRepository.getReferenceById(dto.getIdUnidadBase()));
        producto.setFechaActualizacion(LocalDateTime.now());
        guardarConfiguracionStock(producto, dto);

        List<PresentacionProducto> nuevasPresentaciones =
                mapearPresentaciones(dto.getPresentaciones(), producto, id);
        if (producto.getPresentaciones() != null) {
            producto.getPresentaciones().clear();
        }
        producto.getPresentaciones().addAll(nuevasPresentaciones);

        try {
            return productoMapper.toResponseDto(productoRepository.save(producto));
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException(
                    "No se pudo guardar el producto. Verifica que el código interno y los códigos de barras sean únicos.");
        }
    }

    private void guardarConfiguracionStock(Producto producto, ProductoRequestDto dto) {
        if (dto.getStockMinimo() == null || dto.getStockMinimo().signum() < 0) {
            throw new ResourceConflictException("El stock mínimo es obligatorio y no puede ser negativo");
        }
        if (dto.getStockMaximo() != null && dto.getStockMaximo().compareTo(dto.getStockMinimo()) < 0) {
            throw new ResourceConflictException("El stock máximo debe ser mayor o igual al stock mínimo");
        }
        ConfiguracionStock config = configuracionStockRepository.findByProductoIdProducto(producto.getIdProducto())
                .orElseGet(ConfiguracionStock::new);
        config.setProducto(producto);
        config.setStockMinimo(dto.getStockMinimo());
        config.setStockMaximo(dto.getStockMaximo());
        config.setActivo(true);
        configuracionStockRepository.save(config);
        producto.setConfiguracionStock(config);
    }

    @Override
    @Transactional
    public ProductoResponseDto activarProducto(Long id) {
        return cambiarEstado(id, true);
    }

    @Override
    @Transactional
    public ProductoResponseDto desactivarProducto(Long id) {
        return cambiarEstado(id, false);
    }

    private ProductoResponseDto cambiarEstado(Long id, boolean nuevoEstado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        producto.setEstado(nuevoEstado);
        producto.setFechaActualizacion(LocalDateTime.now());
        return productoMapper.toResponseDto(productoRepository.save(producto));
    }

    private void validarNombreDuplicadoEnEdicion(String nombre, Long idMarca, Long idSubcategoria, Long id) {
        boolean existe = (idMarca == null)
                ? productoRepository.existsByNombreIgnoreCaseAndSubcategoria_IdSubcategoriaAndIdProductoNot(
                        nombre, idSubcategoria, id)
                : productoRepository.existsByNombreIgnoreCaseAndMarca_IdMarcaAndSubcategoria_IdSubcategoriaAndIdProductoNot(
                        nombre, idMarca, idSubcategoria, id);

        if (existe) {
            throw new ResourceConflictException("Ya existe un producto con ese nombre en esa subcategoría");
        }
    }

    private List<PresentacionProducto> mapearPresentaciones(
            List<PresentacionProductoRequestDto> presentacionesDto, Producto producto) {
        return mapearPresentaciones(presentacionesDto, producto, null);
    }

    private List<PresentacionProducto> mapearPresentaciones(
            List<PresentacionProductoRequestDto> presentacionesDto, Producto producto, Long idProductoExcluido) {
        if (presentacionesDto == null || presentacionesDto.isEmpty()) {
            throw new ResourceConflictException("El producto debe tener al menos una presentación de venta");
        }

        validarUnaPrincipal(presentacionesDto);
        validarNombresPresentacionesUnicos(presentacionesDto);
        validarCodigosBarra(presentacionesDto, idProductoExcluido);

        List<PresentacionProducto> presentaciones = new ArrayList<>();
        for (PresentacionProductoRequestDto pDto : presentacionesDto) {
            UnidadMedida unidad = validarUnidadPresentacion(pDto.getIdUnidadMedida());

            PresentacionProducto presentacion = PresentacionProducto.builder()
                    .nombrePresentacion(pDto.getNombrePresentacion().trim())
                    .factorConversion(pDto.getFactorConversion())
                    .precioVenta(pDto.getPrecioVenta())
                    .esPrincipal(pDto.getEsPrincipal() != null && pDto.getEsPrincipal())
                    .estado(true)
                    .producto(producto)
                    .unidadMedida(unidad)
                    .build();

            List<CodigoBarra> codigos = new ArrayList<>();
            List<CodigoBarraRequestDto> codigosDto = obtenerCodigosDto(pDto);
            for (CodigoBarraRequestDto cDto : codigosDto) {
                codigos.add(CodigoBarra.builder()
                        .codigo(cDto.getCodigo().trim())
                        .principal(cDto.getPrincipal() != null && cDto.getPrincipal())
                        .estado(true)
                        .presentacionProducto(presentacion)
                        .build());
            }
            presentacion.setCodigosBarra(codigos);
            presentaciones.add(presentacion);
        }
        return presentaciones;
    }

    private void validarUnaPrincipal(List<PresentacionProductoRequestDto> presentacionesDto) {
        long principales = presentacionesDto.stream()
                .filter(p -> Boolean.TRUE.equals(p.getEsPrincipal()))
                .count();
        if (principales != 1) {
            throw new ResourceConflictException("Debe existir exactamente una presentación principal");
        }
    }

    private void validarNombresPresentacionesUnicos(List<PresentacionProductoRequestDto> presentacionesDto) {
        Set<String> nombres = new HashSet<>();
        for (PresentacionProductoRequestDto p : presentacionesDto) {
            String nombre = p.getNombrePresentacion().trim().toLowerCase(Locale.ROOT);
            if (!nombres.add(nombre)) {
                throw new ResourceConflictException(
                        "No puede haber dos presentaciones con el mismo nombre: " + p.getNombrePresentacion().trim());
            }
        }
    }

    private void validarCodigosBarra(
            List<PresentacionProductoRequestDto> presentacionesDto, Long idProductoExcluido) {
        for (PresentacionProductoRequestDto p : presentacionesDto) {
            if (Boolean.TRUE.equals(p.getUsaCodigo())
                    && (p.getCodigo() == null || p.getCodigo().isBlank())
                    && (p.getCodigos() == null || p.getCodigos().isEmpty())) {
                throw new ResourceConflictException(
                        "Indica el código de barras para la presentación: "
                                + p.getNombrePresentacion().trim());
            }
            for (CodigoBarraRequestDto c : obtenerCodigosDto(p)) {
                String codigo = c.getCodigo().trim();
                if (codigo.isEmpty()) {
                    throw new ResourceConflictException("El código de barras no puede estar vacío");
                }
                boolean existe = (idProductoExcluido == null)
                        ? codigoBarraRepository.existsByCodigo(codigo)
                        : codigoBarraRepository.existsByCodigoAndPresentacionProducto_Producto_IdProductoNot(
                                codigo, idProductoExcluido);
                if (existe) {
                    throw new ResourceConflictException(
                            "Ya existe un producto con el código de barras: " + codigo);
                }
            }
        }
    }

    private UnidadMedida validarUnidadPresentacion(Long idUnidadMedida) {
        UnidadMedida unidad = unidadMedidaRepository.findById(idUnidadMedida)
                .orElseThrow(() -> new EntityNotFoundException("Unidad de medida no encontrada"));
        if (!unidad.getEstado()) {
            throw new ResourceConflictException("No se puede usar una unidad de medida desactivada");
        }
        return unidad;
    }

    private List<CodigoBarraRequestDto> obtenerCodigosDto(PresentacionProductoRequestDto p) {
        if (p.getCodigos() != null && !p.getCodigos().isEmpty()) {
            return p.getCodigos();
        }
        if (Boolean.TRUE.equals(p.getUsaCodigo()) && p.getCodigo() != null && !p.getCodigo().isBlank()) {
            return List.of(CodigoBarraRequestDto.builder().codigo(p.getCodigo()).build());
        }
        return List.of();
    }

    private String trimDescripcion(String descripcion) {
        return (descripcion == null || descripcion.isBlank()) ? null : descripcion.trim();
    }

}
