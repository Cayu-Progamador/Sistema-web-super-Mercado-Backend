package com.backendSupermercado.supermercasdo.modules.inventario.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.backendSupermercado.supermercasdo.modules.inventario.dto.ExistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.inventario.dto.LoteExistenciaDto;
import com.backendSupermercado.supermercasdo.modules.inventario.dto.InventarioResumenDto;
import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.inventario.service.InventarioService;
import com.backendSupermercado.supermercasdo.modules.inventario.service.ReporteInventarioPdfService;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ConfiguracionStockRepository;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {
    
    private final ExistenciaRepository existenciaRepository;
    private final ConfiguracionStockRepository configuracionRepository;
    private final ReporteInventarioPdfService reportePdfService;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarExistenciasPdf(String buscar, String almacen, String ubicacion,
            String categoria, String estado, String lote, java.time.LocalDate vencimientoHasta) {
        var datos = new ArrayList<ExistenciaResponseDto>();
        int numeroPagina = 0;
        Page<ExistenciaResponseDto> pagina;

        do {
            pagina = listarExistencias(buscar, almacen, ubicacion, categoria, estado, lote,
                    vencimientoHasta, PageRequest.of(numeroPagina++, 100,
                            Sort.by(Sort.Direction.ASC, "producto.nombre")));
            datos.addAll(pagina.getContent());
        } while (!pagina.isLast());

        String filtros = "Filtros: " + String.join(" · ", java.util.stream.Stream.of(
                buscar == null || buscar.isBlank() ? null : "búsqueda '" + buscar.trim() + "'",
                almacen == null || almacen.isBlank() ? null : "almacén " + almacen,
                ubicacion == null || ubicacion.isBlank() ? null : "ubicación " + ubicacion,
                categoria == null || categoria.isBlank() ? null : "categoría " + categoria,
                estado == null || estado.isBlank() ? null : "estado " + estado,
                lote == null || lote.isBlank() ? null : "lote " + lote,
                vencimientoHasta == null ? null : "vence hasta " + vencimientoHasta)
                .filter(java.util.Objects::nonNull).toList());
        if ("Filtros: ".equals(filtros)) filtros = "Filtros: todas las existencias";

        return reportePdfService.generarExistencias(datos, filtros);
    }
  
    @Override @Transactional(readOnly = true)
    public InventarioResumenDto obtenerResumen() {
        var hoy = java.time.LocalDate.now(); var limite = hoy.plusDays(30); long stock=0,bajo=0,sin=0,vencen=0;
        for (Existencia e : existenciaRepository.findAll()) { var cfg=configuracionRepository.findByProductoIdProducto(e.getProducto().getIdProducto()).orElse(null); var min=cfg==null?BigDecimal.ZERO:cfg.getStockMinimo(); var q=e.getCantidad(); if(q.signum()>0) stock++; if(q.signum()==0) sin++; else if(q.compareTo(min)<=0) bajo++; if(e.getLote()!=null&&e.getLote().getFechaVencimiento()!=null&&!e.getLote().getFechaVencimiento().isBefore(hoy)&&!e.getLote().getFechaVencimiento().isAfter(limite)) vencen++; }
        return new InventarioResumenDto(stock,bajo,sin,vencen);
    }
    
    @Override @Transactional(readOnly = true)
    public ExistenciaResponseDto obtenerDetalle(Long id) {
        Existencia e = existenciaRepository.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Existencia no encontrada: " + id));
        return toDto(e, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExistenciaResponseDto> listarExistencias(String buscar, String almacen, String ubicacion,
            String categoria, String estado,
            String lote, java.time.LocalDate vencimientoHasta, Pageable pageable) {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() < 1 || pageable.getPageSize() > 100)
            throw new IllegalArgumentException("La paginación no es válida");
        Specification<Existencia> spec = Specification.where(null);
        if (buscar != null && !buscar.isBlank()) {
            String q = "%" + buscar.trim().toLowerCase() + "%";
            spec = spec.and((r, c, cb) -> cb.or(cb.like(cb.lower(r.join("producto").get("nombre")), q),
                    cb.like(cb.lower(r.join("producto").get("codigoInterno")), q)));
        }
        if (almacen != null && !almacen.isBlank())
            spec = spec.and((r, c, cb) -> {
                var a = r.join("ubicacion").join("almacen");
                return almacen.matches("\\d+") ? cb.equal(a.get("idAlmacen"), Long.valueOf(almacen))
                        : cb.equal(cb.lower(a.get("nombre")), almacen.trim().toLowerCase());
            });
        if (ubicacion != null && !ubicacion.isBlank())
            spec = spec.and((r, c, cb) -> {
                var u = r.join("ubicacion");
                return ubicacion.matches("\\d+") ? cb.equal(u.get("idUbicacion"), Long.valueOf(ubicacion))
                        : cb.or(cb.equal(cb.lower(u.get("nombre")), ubicacion.trim().toLowerCase()),
                                cb.equal(cb.lower(u.get("codigo")), ubicacion.trim().toLowerCase()));
            });
        if (lote != null && !lote.isBlank())
            spec = spec.and((r, c, cb) -> cb.like(cb.lower(r.join("lote").get("codigoLote")),
                    "%" + lote.trim().toLowerCase() + "%"));
        if (categoria != null && !categoria.isBlank())
            spec = spec.and((r, c, cb) -> cb.equal(
                    cb.lower(r.join("producto").join("subcategoria").join("categoria").get("nombre")),
                    categoria.trim().toLowerCase()));
        if (vencimientoHasta != null)
            spec = spec
                    .and((r, c, cb) -> cb.lessThanOrEqualTo(r.join("lote").get("fechaVencimiento"), vencimientoHasta));
        if (estado != null && !estado.isBlank()) {
            String s = estado.trim().toUpperCase().replace(' ', '_');
            spec = spec.and((r, c, cb) -> {
                jakarta.persistence.criteria.Expression<BigDecimal> cantidad = r.<BigDecimal>get("cantidad");
                var cfg = r.join("producto").join("configuracionStock");
                jakarta.persistence.criteria.Expression<BigDecimal> min = cfg.<BigDecimal>get("stockMinimo");
                jakarta.persistence.criteria.Expression<BigDecimal> max = cfg.<BigDecimal>get("stockMaximo");
                return switch (s) {
                    case "SIN_STOCK" -> cb.equal(cantidad, BigDecimal.ZERO);
                    case "STOCK_BAJO" ->
                        cb.and(cb.greaterThan(cantidad, BigDecimal.ZERO), cb.lessThanOrEqualTo(cantidad, min));
                    case "SOBRESTOCK" -> cb.and(cb.isNotNull(max), cb.greaterThan(cantidad, max));
                    default -> cb.and(cb.greaterThan(cantidad, BigDecimal.ZERO), cb.greaterThan(cantidad, min),
                            cb.or(cb.isNull(max), cb.lessThanOrEqualTo(cantidad, max)));
                };
            });
        }
        Page<Existencia> page = existenciaRepository.findAll(spec, pageable);
        return page.map(e -> toDto(e, estado));
    }

    private ExistenciaResponseDto toDto(Existencia e, String filtroEstado) {
        var cfg = configuracionRepository.findByProductoIdProducto(e.getProducto().getIdProducto()).orElse(null);
        BigDecimal min = cfg == null ? BigDecimal.ZERO : cfg.getStockMinimo(),
                max = cfg == null ? null : cfg.getStockMaximo();
        String estado = calcularEstado(e.getCantidad(), min, max);
        var u = e.getUbicacion();
        var l = e.getLote();
        var p = e.getProducto();
        return new ExistenciaResponseDto(e.getIdExistencia(), p.getIdProducto(), p.getCodigoInterno(), p.getNombre(),
                u.getIdUbicacion(), u.getCodigo(), u.getNombre(), u.getAlmacen().getNombre(),
                l == null ? null : l.getIdLote(), l == null ? null : l.getCodigoLote(),
                l == null ? null : l.getFechaVencimiento(), e.getCantidad(), min, max,
                p.getUnidadBase().getNombre(), estado, e.getLote() == null ? java.util.List.of() : java.util.List.of(new LoteExistenciaDto(l.getIdLote(), l.getCodigoLote(), e.getCantidad(), u.getAlmacen().getNombre(), u.getCodigo(), l.getFechaVencimiento())));
    }

    private String calcularEstado(BigDecimal cantidad, BigDecimal min, BigDecimal max) {
        if (cantidad == null || cantidad.signum() == 0)
            return "SIN_STOCK";
        if (min != null && cantidad.compareTo(min) <= 0)
            return "STOCK_BAJO";
        if (max != null && cantidad.compareTo(max) > 0)
            return "SOBRESTOCK";
        return "DISPONIBLE";
    }
}
