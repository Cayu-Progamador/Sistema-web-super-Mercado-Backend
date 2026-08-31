package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.service.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.UbicacionListadoDto;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.ActualizarUbicacionDto;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.UbicacionAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.service.UbicacionAlmacenService;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.specification.UbicacionSpecification;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.ProductoUbicacionDto;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.dto.CrearUbicacionDto;
import com.backendSupermercado.supermercasdo.modules.almacen.repository.AlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.TipoUbicacionRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.service.UbicacionInventarioPdfService;

@Service @RequiredArgsConstructor
public class UbicacionAlmacenServiceImpl implements UbicacionAlmacenService {
    private final UbicacionAlmacenRepository ubicacionRepository;
    private final ExistenciaRepository existenciaRepository;
    private final AlmacenRepository almacenRepository;
    private final TipoUbicacionRepository tipoUbicacionRepository;
    private final UbicacionInventarioPdfService ubicacionPdfService;

    @Override @Transactional(readOnly = true)
    public byte[] generarPdf(Long id, String usuario) {
        UbicacionAlmacen u = buscar(id);
        return ubicacionPdfService.generar(u, existenciaRepository.findByUbicacion_IdUbicacionIn(java.util.List.of(id)), usuario);
    }

    @Override @Transactional
    public UbicacionListadoDto crear(CrearUbicacionDto dto) {
        var almacen = almacenRepository.findById(dto.getIdAlmacen())
                .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));
        if (!Boolean.TRUE.equals(almacen.getActivo())) {
            throw new ResourceConflictException("No se puede crear una ubicación en un almacén inactivo");
        }
        String nombre = dto.getNombre().trim();
        if (ubicacionRepository.existsByAlmacenAndNombreIgnoreCase(almacen, nombre)) {
            throw new ResourceConflictException("Ya existe una ubicación con ese nombre en el almacén");
        }
        String tipo = dto.getTipo().trim().toUpperCase();
        if (tipoUbicacionRepository.findByActivoTrueOrderByNombreAsc().stream()
                .noneMatch(t -> t.getNombre().equalsIgnoreCase(tipo))) {
            throw new ResourceConflictException("El tipo de ubicación no es válido");
        }
        long siguiente = ubicacionRepository.findTopByOrderByIdUbicacionDesc()
                .map(u -> u.getIdUbicacion() + 1).orElse(1L);
        UbicacionAlmacen nueva = UbicacionAlmacen.builder()
                .almacen(almacen).codigo(String.format("UBI-%03d", siguiente))
                .nombre(nombre).tipo(tipo).descripcion(dto.getDescripcion()).activo(true).build();
        return convertir(nuevaGuardada(nueva), java.util.List.of());
    }

    private UbicacionAlmacen nuevaGuardada(UbicacionAlmacen ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    @Override @Transactional
    public UbicacionListadoDto actualizar(Long id, ActualizarUbicacionDto dto) {
        UbicacionAlmacen u = ubicacionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ubicación no encontrada"));
        String nombre = dto.getNombre().trim();
        if (ubicacionRepository.existsByAlmacenAndNombreIgnoreCaseAndIdUbicacionNot(u.getAlmacen(), nombre, id)) {
            throw new ResourceConflictException("Ya existe una ubicación con ese nombre en el almacén");
        }
        String tipo = dto.getTipo() == null ? null : dto.getTipo().trim().toUpperCase();
        if (tipo == null || tipoUbicacionRepository.findByActivoTrueOrderByNombreAsc().stream().noneMatch(t -> t.getNombre().equalsIgnoreCase(tipo))) {
            throw new ResourceConflictException("El tipo de ubicación no es válido");
        }
        u.setNombre(nombre); u.setTipo(tipo); u.setDescripcion(dto.getDescripcion());
        return convertir(u, existenciaRepository.findByUbicacion_IdUbicacionIn(java.util.List.of(id)));
    }
    @Override @Transactional
    public UbicacionListadoDto activar(Long id) { UbicacionAlmacen u = buscar(id); if (Boolean.TRUE.equals(u.getActivo())) throw new ResourceConflictException("La ubicación ya está activa"); u.setActivo(true); return convertir(u, existenciaRepository.findByUbicacion_IdUbicacionIn(java.util.List.of(id))); }
    @Override @Transactional
    public UbicacionListadoDto desactivar(Long id) { UbicacionAlmacen u = buscar(id); if (!Boolean.TRUE.equals(u.getActivo())) throw new ResourceConflictException("La ubicación ya está inactiva"); if (existenciaRepository.findByUbicacion_IdUbicacionIn(java.util.List.of(id)).stream().anyMatch(e -> e.getCantidad().compareTo(BigDecimal.ZERO)>0)) throw new ResourceConflictException("No se puede desactivar la ubicación porque todavía contiene productos"); u.setActivo(false); return convertir(u, java.util.List.of()); }
    private UbicacionAlmacen buscar(Long id) { return ubicacionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ubicación no encontrada")); }

    @Override @Transactional(readOnly = true)
    public java.util.List<ProductoUbicacionDto> listarProductos(Long id) {
        buscar(id);
        return existenciaRepository.findByUbicacion_IdUbicacionIn(java.util.List.of(id)).stream()
                .filter(e -> e.getCantidad().compareTo(BigDecimal.ZERO) > 0)
                .map(e -> new ProductoUbicacionDto(e.getProducto().getIdProducto(), e.getLote() != null ? e.getLote().getIdLote() : null, e.getProducto().getNombre(), e.getLote() != null ? e.getLote().getCodigoLote() : "-", e.getCantidad(), e.getProducto().getUnidadBase() != null ? e.getProducto().getUnidadBase().getAbreviatura() : "-", e.getLote() != null ? e.getLote().getFechaVencimiento() : null, estadoProducto(e.getLote() != null ? e.getLote().getFechaVencimiento() : null, e.getCantidad())))
                .toList();
    }
    private String estadoProducto(LocalDate vencimiento, BigDecimal cantidad) { if (vencimiento != null && vencimiento.isBefore(LocalDate.now())) return "VENCIDO"; if (vencimiento != null && !vencimiento.isAfter(LocalDate.now().plusDays(30))) return "PROXIMO_VENCER"; return cantidad.compareTo(BigDecimal.TEN) <= 0 ? "STOCK_BAJO" : "DISPONIBLE"; }

    @Override @Transactional(readOnly = true)
    public Page<UbicacionListadoDto> listar(String search, Long idAlmacen, String tipo, Boolean estado, Pageable pageable) {
        Page<UbicacionAlmacen> pagina = ubicacionRepository.findAll(UbicacionSpecification.filtros(search, idAlmacen, tipo, estado), pageable);
        if (pagina.isEmpty()) return pagina.map(this::convertirVacia);
        var ids = pagina.getContent().stream().map(UbicacionAlmacen::getIdUbicacion).toList();
        Map<Long, java.util.List<Existencia>> existencias = existenciaRepository.findByUbicacion_IdUbicacionIn(ids).stream()
                .collect(Collectors.groupingBy(e -> e.getUbicacion().getIdUbicacion()));
        return pagina.map(u -> convertir(u, existencias.getOrDefault(u.getIdUbicacion(), java.util.List.of())));
    }

    private UbicacionListadoDto convertirVacia(UbicacionAlmacen u) { return convertir(u, java.util.List.of()); }
    private UbicacionListadoDto convertir(UbicacionAlmacen u, java.util.List<Existencia> lista) {
        long productos = lista.stream().filter(e -> e.getCantidad().compareTo(BigDecimal.ZERO) > 0).map(e -> e.getProducto().getIdProducto()).distinct().count();
        BigDecimal stock = lista.stream().map(Existencia::getCantidad).reduce(BigDecimal.ZERO, BigDecimal::add);
        return UbicacionListadoDto.builder().idUbicacion(u.getIdUbicacion()).codigo(u.getCodigo()).nombre(u.getNombre()).descripcion(u.getDescripcion())
                .idAlmacen(u.getAlmacen().getIdAlmacen()).nombreAlmacen(u.getAlmacen().getNombre())
                .tipo(u.getTipo()).activo(u.getActivo()).productos(productos).stockTotal(stock).build();
    }
}
