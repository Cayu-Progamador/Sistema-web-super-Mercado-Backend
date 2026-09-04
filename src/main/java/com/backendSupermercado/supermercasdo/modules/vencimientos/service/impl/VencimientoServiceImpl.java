package com.backendSupermercado.supermercasdo.modules.vencimientos.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoFiltroDto;
import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoResponseDto;
import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoResumenDto;
import com.backendSupermercado.supermercasdo.modules.vencimientos.model.VencimientoEstado;
import com.backendSupermercado.supermercasdo.modules.vencimientos.service.VencimientoService;
import com.backendSupermercado.supermercasdo.modules.vencimientos.specification.VencimientoSpecification;
import com.backendSupermercado.supermercasdo.modules.inventario.service.ReporteInventarioPdfService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VencimientoServiceImpl implements VencimientoService {

    private final ExistenciaRepository existenciaRepository;
    private final ReporteInventarioPdfService reportePdfService;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarPdf(VencimientoFiltroDto filtros) {
        validarRangoFechas(filtros);

        var datos = new ArrayList<VencimientoResponseDto>();
        int numeroPagina = 0;
        Page<VencimientoResponseDto> pagina;
        do {
            pagina = listar(filtros, PageRequest.of(numeroPagina++, 100,
                    Sort.by(Sort.Direction.ASC, "lote.fechaVencimiento")
                            .and(Sort.by(Sort.Direction.ASC, "producto.nombre"))));
            datos.addAll(pagina.getContent());
        } while (!pagina.isLast());

        String descripcion = "Filtros: " + String.join(" · ", java.util.stream.Stream.of(
                filtros.buscar() == null || filtros.buscar().isBlank()
                        ? null : "búsqueda '" + filtros.buscar().trim() + "'",
                filtros.estado() == null || filtros.estado().isBlank()
                        ? null : "estado " + filtros.estado(),
                filtros.fechaDesde() == null ? null : "desde " + filtros.fechaDesde(),
                filtros.fechaHasta() == null ? null : "hasta " + filtros.fechaHasta())
                .filter(java.util.Objects::nonNull).toList());
        if ("Filtros: ".equals(descripcion)) descripcion = "Filtros: todos los vencimientos";

        return reportePdfService.generarVencimientos(datos, descripcion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VencimientoResponseDto> listar(
            VencimientoFiltroDto filtros,
            Pageable pageable) {
        validarRangoFechas(filtros);

        LocalDate hoy = LocalDate.now();
        return existenciaRepository
                .findAll(VencimientoSpecification.conFiltros(filtros, hoy), pageable)
                .map(existencia -> convertirDto(existencia, hoy));
    }

    @Override
    @Transactional(readOnly = true)
    public VencimientoResumenDto obtenerResumen() {
        LocalDate hoy = LocalDate.now();

        long vencidos = existenciaRepository.contarLotesVencidos(hoy);
        long vencenHoy = existenciaRepository.contarLotesQueVencenEn(hoy);
        long proximos7Dias = existenciaRepository.contarLotesPorVencer(
                hoy.plusDays(1),
                hoy.plusDays(7));
        long proximos30Dias = existenciaRepository.contarLotesPorVencer(
                hoy.plusDays(8),
                hoy.plusDays(30));

        return new VencimientoResumenDto(
                vencidos,
                vencenHoy,
                proximos7Dias,
                proximos30Dias);
    }

    private void validarRangoFechas(VencimientoFiltroDto filtros) {
        if (filtros.fechaDesde() != null
                && filtros.fechaHasta() != null
                && filtros.fechaDesde().isAfter(filtros.fechaHasta())) {
            throw new IllegalArgumentException(
                    "La fecha desde no puede ser posterior a la fecha hasta");
        }
    }

    private VencimientoResponseDto convertirDto(Existencia existencia, LocalDate hoy) {
        var producto = existencia.getProducto();
        var lote = existencia.getLote();
        var ubicacion = existencia.getUbicacion();
        LocalDate fechaVencimiento = lote.getFechaVencimiento();

        return new VencimientoResponseDto(
                existencia.getIdExistencia(),
                producto.getIdProducto(),
                producto.getCodigoInterno(),
                producto.getNombre(),
                lote.getIdLote(),
                lote.getCodigoLote(),
                fechaVencimiento,
                ChronoUnit.DAYS.between(hoy, fechaVencimiento),
                existencia.getCantidad(),
                producto.getUnidadBase().getAbreviatura(),
                ubicacion.getAlmacen().getNombre(),
                ubicacion.getCodigo(),
                VencimientoEstado.calcular(fechaVencimiento, hoy).etiqueta());
    }
}
