package com.backendSupermercado.supermercasdo.modules.inventario_fisico.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.entity.InventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;

import jakarta.persistence.criteria.JoinType;

public final class InventarioFisicoSpecification {

    private InventarioFisicoSpecification() {
    }

    public static Specification<InventarioFisico> filtrar(
            String buscar,
            EstadoInventarioFisico estado,
            Long idAlmacen,
            LocalDate fechaDesde,
            LocalDate fechaHasta) {

        return Specification.where(contieneTexto(buscar))
                .and(tieneEstado(estado))
                .and(perteneceAlAlmacen(idAlmacen))
                .and(programadoDesde(fechaDesde))
                .and(programadoHasta(fechaHasta));
    }

    private static Specification<InventarioFisico> contieneTexto(String buscar) {
        return (root, query, cb) -> {
            if (buscar == null || buscar.isBlank()) {
                return cb.conjunction();
            }

            String patron = "%" + buscar.trim().toLowerCase() + "%";
            var responsable = root.join("responsable", JoinType.INNER);
            var persona = responsable.join("persona", JoinType.INNER);

            return cb.or(
                    cb.like(cb.lower(root.get("codigo")), patron),
                    cb.like(cb.lower(persona.get("nombres")), patron),
                    cb.like(cb.lower(cb.coalesce(persona.get("apellidoPaterno"), "")), patron),
                    cb.like(cb.lower(cb.coalesce(persona.get("apellidoMaterno"), "")), patron));
        };
    }

    private static Specification<InventarioFisico> tieneEstado(EstadoInventarioFisico estado) {
        return (root, query, cb) -> estado == null
                ? cb.conjunction()
                : cb.equal(root.get("estado"), estado);
    }

    private static Specification<InventarioFisico> perteneceAlAlmacen(Long idAlmacen) {
        return (root, query, cb) -> idAlmacen == null
                ? cb.conjunction()
                : cb.equal(root.join("almacen", JoinType.INNER).get("idAlmacen"), idAlmacen);
    }

    private static Specification<InventarioFisico> programadoDesde(LocalDate fechaDesde) {
        return (root, query, cb) -> fechaDesde == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("fechaProgramada"), fechaDesde);
    }

    private static Specification<InventarioFisico> programadoHasta(LocalDate fechaHasta) {
        return (root, query, cb) -> fechaHasta == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("fechaProgramada"), fechaHasta);
    }
}
