package com.backendSupermercado.supermercasdo.modules.vencimientos.specification;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoFiltroDto;
import com.backendSupermercado.supermercasdo.modules.vencimientos.model.VencimientoEstado;

public final class VencimientoSpecification {

    private VencimientoSpecification() {
    }

    public static Specification<Existencia> conFiltros(
            VencimientoFiltroDto filtros,
            LocalDate hoy) {
        return Specification
                .where(conStock())
                .and(conFechaVencimiento())
                .and(buscar(filtros.buscar()))
                .and(fechaDesde(filtros.fechaDesde()))
                .and(fechaHasta(filtros.fechaHasta()))
                .and(estado(VencimientoEstado.desdeTexto(filtros.estado()), hoy));
    }

    private static Specification<Existencia> conStock() {
        return (root, query, cb) -> cb.greaterThan(root.get("cantidad"), BigDecimal.ZERO);
    }

    private static Specification<Existencia> conFechaVencimiento() {
        return (root, query, cb) -> cb.isNotNull(root.get("lote").get("fechaVencimiento"));
    }

    private static Specification<Existencia> buscar(String texto) {
        return (root, query, cb) -> {
            if (texto == null || texto.isBlank()) {
                return cb.conjunction();
            }

            String patron = "%" + texto.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("producto").get("codigoInterno")), patron),
                    cb.like(cb.lower(root.get("producto").get("nombre")), patron),
                    cb.like(cb.lower(root.get("lote").get("codigoLote")), patron));
        };
    }

    private static Specification<Existencia> fechaDesde(LocalDate fecha) {
        return (root, query, cb) -> fecha == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("lote").get("fechaVencimiento"), fecha);
    }

    private static Specification<Existencia> fechaHasta(LocalDate fecha) {
        return (root, query, cb) -> fecha == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("lote").get("fechaVencimiento"), fecha);
    }

    private static Specification<Existencia> estado(
            VencimientoEstado estado,
            LocalDate hoy) {
        return (root, query, cb) -> {
            if (estado == null) {
                return cb.conjunction();
            }

            var fecha = root.get("lote").<LocalDate>get("fechaVencimiento");
            return switch (estado) {
                case VENCIDO -> cb.lessThan(fecha, hoy);
                case VENCE_HOY -> cb.equal(fecha, hoy);
                case URGENTE -> cb.between(fecha, hoy.plusDays(1), hoy.plusDays(7));
                case PROXIMO -> cb.between(fecha, hoy.plusDays(8), hoy.plusDays(30));
                case NORMAL -> cb.greaterThan(fecha, hoy.plusDays(30));
            };
        };
    }
}
