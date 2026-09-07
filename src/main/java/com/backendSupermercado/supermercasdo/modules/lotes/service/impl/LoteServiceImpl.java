package com.backendSupermercado.supermercasdo.modules.lotes.service.impl;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.lotes.dto.LoteResponseDto;
import com.backendSupermercado.supermercasdo.modules.lotes.dto.LoteUpdateRequestDto;
import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.lotes.entity.Lote;
import com.backendSupermercado.supermercasdo.modules.lotes.repository.LoteRepository;
import com.backendSupermercado.supermercasdo.modules.lotes.service.LoteService;
import com.backendSupermercado.supermercasdo.modules.inventario.service.ReporteInventarioPdfService;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.DetalleRecepcion;
import com.backendSupermercado.supermercasdo.modules.recepcion.repository.DetalleRecepcionRepository;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;

@Service
@RequiredArgsConstructor
public class LoteServiceImpl implements LoteService {
    private final LoteRepository repo;
    private final ExistenciaRepository existenciaRepository;
    private final ReporteInventarioPdfService reportePdfService;
    private final DetalleRecepcionRepository detalleRecepcionRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarPdf(String buscar, Long idProducto, String estado, LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }

        var datos = new ArrayList<LoteResponseDto>();
        int numeroPagina = 0;
        Page<LoteResponseDto> pagina;
        do {
            pagina = listar(buscar, idProducto, estado, desde, hasta,
                    PageRequest.of(numeroPagina++, 100, Sort.by("fechaVencimiento").ascending()));
            datos.addAll(pagina.getContent());
        } while (!pagina.isLast());

        String filtros = "Filtros: " + String.join(" · ", java.util.stream.Stream.of(
                buscar == null || buscar.isBlank() ? null : "búsqueda '" + buscar.trim() + "'",
                idProducto == null ? null : "producto seleccionado",
                estado == null || estado.isBlank() ? null : "estado " + estado,
                desde == null ? null : "desde " + desde,
                hasta == null ? null : "hasta " + hasta)
                .filter(java.util.Objects::nonNull).toList());
        if ("Filtros: ".equals(filtros)) filtros = "Filtros: todos los lotes";

        return reportePdfService.generarLotes(datos, filtros);
    }

    @Override @Transactional
    public LoteResponseDto actualizar(Long idLote, LoteUpdateRequestDto dto) {
        Lote lote = repo.findById(idLote).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Lote no encontrado: " + idLote));
        String codigo = dto.codigoLote().trim();
        if (!codigo.equalsIgnoreCase(lote.getCodigoLote()) && repo.existsByProducto_IdProductoAndCodigoLote(lote.getProducto().getIdProducto(), codigo)) throw new ResourceConflictException("Ya existe otro lote con ese código para el producto");
        if (dto.fechaFabricacion() != null && dto.fechaVencimiento() != null && dto.fechaVencimiento().isBefore(dto.fechaFabricacion())) throw new IllegalArgumentException("La fecha de vencimiento debe ser posterior a la fecha de fabricación");
        lote.setCodigoLote(codigo); lote.setFechaFabricacion(dto.fechaFabricacion()); lote.setFechaVencimiento(dto.fechaVencimiento()); lote.setObservacion(dto.observacion());
        Lote guardado = repo.save(lote); String estado = guardado.getActivo() != null && !guardado.getActivo() ? "BLOQUEADO" : guardado.getFechaVencimiento() != null && guardado.getFechaVencimiento().isBefore(LocalDate.now()) ? "VENCIDO" : guardado.getFechaVencimiento() != null && !guardado.getFechaVencimiento().isAfter(LocalDate.now().plusDays(30)) ? "POR_VENCER" : "ACTIVO";
        var ex = existenciaRepository.findFirstByLote_IdLote(guardado.getIdLote()).orElse(null);
        return toDto(guardado, estado, ex);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoteResponseDto> listar(String buscar, Long idProducto, String estado, LocalDate desde, LocalDate hasta,
            Pageable pageable) {
        Specification<Lote> s = Specification.where(null);
        if (buscar != null && !buscar.isBlank())
            s = s.and((r, c, cb) -> cb.or(
                    cb.like(cb.lower(r.join("producto").get("nombre")), "%" + buscar.toLowerCase().trim() + "%"),
                    cb.like(cb.lower(r.get("codigoLote")), "%" + buscar.toLowerCase().trim() + "%")));
        if (idProducto != null)
            s = s.and((r, c, cb) -> cb.equal(r.join("producto").get("idProducto"), idProducto));
        if (desde != null)
            s = s.and((r, c, cb) -> cb.greaterThanOrEqualTo(r.get("fechaVencimiento"), desde));
        if (hasta != null)
            s = s.and((r, c, cb) -> cb.lessThanOrEqualTo(r.get("fechaVencimiento"), hasta));
        Page<Lote> p = repo.findAll(s, pageable);
        return p.map(l -> {
            String e = l.getActivo() != null && !l.getActivo() ? "BLOQUEADO"
                    : l.getFechaVencimiento() != null && l.getFechaVencimiento().isBefore(LocalDate.now()) ? "VENCIDO"
                            : l.getFechaVencimiento() != null
                                    && !l.getFechaVencimiento().isAfter(LocalDate.now().plusDays(30)) ? "POR_VENCER"
                                            : "ACTIVO";
            return toDto(l, e, existenciaRepository.findFirstByLote_IdLote(l.getIdLote()).orElse(null));
        });
    }
    private LoteResponseDto toDto(Lote l, String estado, com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia ex) {
        BigDecimal costo = detalleRecepcionRepository.findFirstByLote_IdLoteOrderByRecepcion_FechaRecepcionDesc(l.getIdLote())
                .map(d -> costoBase(d, l.getProducto())).orElse(null);
        return new LoteResponseDto(l.getIdLote(), l.getCodigoLote(), l.getProducto().getIdProducto(), l.getProducto().getNombre(),
                l.getFechaIngreso(), l.getFechaFabricacion(), l.getFechaVencimiento(), existenciaRepository.sumarCantidadPorLote(l.getIdLote()),
                l.getActivo(), estado, ex == null ? null : ex.getUbicacion().getAlmacen().getNombre(), ex == null ? null : ex.getUbicacion().getCodigo(), costo);
    }
    private BigDecimal costoBase(DetalleRecepcion detalle, Producto producto) {
        BigDecimal factor = producto.getPresentaciones() == null ? null : producto.getPresentaciones().stream()
                .filter(p -> p.getUnidadMedida() != null && detalle.getUnidadMedida() != null
                        && p.getUnidadMedida().getIdUnidadMedida().equals(detalle.getUnidadMedida().getIdUnidadMedida()))
                .map(p -> p.getFactorConversion()).filter(java.util.Objects::nonNull)
                .findFirst().orElse(BigDecimal.ONE);
        return detalle.getPrecioUnitario().divide(factor, 4, java.math.RoundingMode.HALF_UP);
    }
    private String distribucion(Lote lote, boolean almacen) { return existenciaRepository.findFirstByLote_IdLote(lote.getIdLote()).map(e -> almacen ? e.getUbicacion().getAlmacen().getNombre() : e.getUbicacion().getCodigo()).orElse(null); }
}
