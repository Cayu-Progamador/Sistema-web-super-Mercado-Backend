package com.backendSupermercado.supermercasdo.modules.kardex.service.impl;

import static com.backendSupermercado.supermercasdo.modules.kardex.specification.KardexSpecification.buscar;
import static com.backendSupermercado.supermercasdo.modules.kardex.specification.KardexSpecification.fechaDesde;
import static com.backendSupermercado.supermercasdo.modules.kardex.specification.KardexSpecification.fechaHasta;
import static com.backendSupermercado.supermercasdo.modules.kardex.specification.KardexSpecification.lote;
import static com.backendSupermercado.supermercasdo.modules.kardex.specification.KardexSpecification.producto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.kardex.dto.KardexFiltroDto;
import com.backendSupermercado.supermercasdo.modules.kardex.dto.KardexResponseDto;
import com.backendSupermercado.supermercasdo.modules.kardex.repository.DetalleMovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.kardex.service.KardexService;
import com.backendSupermercado.supermercasdo.modules.kardex.service.KardexPdfService;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.DetalleMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.NaturalezaMovimiento;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KardexServiceImpl implements KardexService {

    private final DetalleMovimientoAlmacenRepository repository;
    private final KardexPdfService kardexPdfService;

    @Override
    @Transactional(readOnly = true)
    public Page<KardexResponseDto> listar(KardexFiltroDto filtros, Pageable pageable) {
        validarFechas(filtros);

        Specification<DetalleMovimientoAlmacen> specification = crearSpecification(filtros);

        return repository.findAll(specification, pageable).map(this::convertirDto);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarPdf(KardexFiltroDto filtros) {
        validarFechas(filtros);

        Specification<DetalleMovimientoAlmacen> specification = crearSpecification(filtros);
        Sort orden = Sort.by(Sort.Direction.DESC, "movimiento.fechaMovimiento")
                .and(Sort.by(Sort.Direction.DESC, "idDetalleMovimiento"));

        var movimientos = repository.findAll(specification, orden).stream()
                .map(this::convertirDto)
                .toList();

        return kardexPdfService.generar(movimientos, filtros);
    }

    private Specification<DetalleMovimientoAlmacen> crearSpecification(KardexFiltroDto filtros) {
        return Specification
                .where(buscar(filtros.buscar()))
                .and(producto(filtros.idProducto()))
                .and(lote(filtros.idLote()))
                .and(fechaDesde(filtros.fechaDesde()))
                .and(fechaHasta(filtros.fechaHasta()));
    }

    private void validarFechas(KardexFiltroDto filtros) {
        if (filtros.fechaDesde() != null
                && filtros.fechaHasta() != null
                && filtros.fechaDesde().isAfter(filtros.fechaHasta())) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }
    }

    private KardexResponseDto convertirDto(DetalleMovimientoAlmacen detalle) {
        var movimiento = detalle.getMovimiento();
        var tipoMovimiento = movimiento.getTipoMovimiento();
        var producto = detalle.getProducto();
        var lote = detalle.getLote();
        var ubicacion = detalle.getUbicacion();
        boolean esEntrada = tipoMovimiento.getNaturaleza() == NaturalezaMovimiento.E;

        return new KardexResponseDto(
                detalle.getIdDetalleMovimiento(),
                movimiento.getFechaMovimiento(),
                producto.getIdProducto(),
                producto.getCodigoInterno(),
                producto.getNombre(),
                movimiento.getReferencia(),
                tipoMovimiento.getNombre(),
                lote != null ? lote.getIdLote() : null,
                lote != null ? lote.getCodigoLote() : null,
                ubicacion.getAlmacen().getNombre(),
                ubicacion.getNombre(),
                esEntrada ? detalle.getCantidad() : null,
                esEntrada ? null : detalle.getCantidad(),
                detalle.getSaldoResultante(),
                producto.getUnidadBase().getAbreviatura(),
                movimiento.getUsuario().getUsername(),
                obtenerObservacion(detalle));
    }

    private String obtenerObservacion(DetalleMovimientoAlmacen detalle) {
        if (detalle.getObservacion() != null && !detalle.getObservacion().isBlank()) {
            return detalle.getObservacion();
        }
        return detalle.getMovimiento().getObservacion();
    }
}
