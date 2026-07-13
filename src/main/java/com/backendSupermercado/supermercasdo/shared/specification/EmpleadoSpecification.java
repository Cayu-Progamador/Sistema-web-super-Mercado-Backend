package com.backendSupermercado.supermercasdo.shared.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;

public class EmpleadoSpecification {

    public static Specification<Empleado> busquedaGeneral(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) return null;
            String pattern = "%" + termino.toLowerCase() + "%";

            var persona = root.join("persona", JoinType.LEFT);

            Predicate nombrePredicate = cb.like(cb.lower(persona.get("nombres")), pattern);

            Predicate apellidoPaternoPredicate = cb.like(cb.lower(persona.get("apellidoPaterno")), pattern);

            Predicate apellidoMaternoPredicate = cb.like(cb.lower(persona.get("apellidoMaterno")), pattern);

            Predicate ciPredicate = cb.like(cb.lower(persona.get("ci")), pattern);

            Predicate nombreCompletoPredicate = cb.like(
                cb.lower(
                    cb.concat(
                        cb.concat(
                            cb.concat(persona.get("nombres"), " "),
                            cb.concat(
                                cb.coalesce(persona.get("apellidoPaterno"), ""),
                                " "
                            )
                        ),
                        cb.coalesce(persona.get("apellidoMaterno"), "")
                    )
                ),
                pattern
            );

            Subquery<Long> subquery = query.subquery(Long.class);
            var subRoot = subquery.from(Contacto.class);
            subquery.select(cb.literal(1L))
                .where(cb.and(
                    cb.equal(subRoot.get("persona").get("idPersona"), persona.get("idPersona")),
                    cb.like(cb.lower(subRoot.get("telefono")), pattern)
                ));
            Predicate telefonoPredicate = cb.exists(subquery);

            return cb.or(
                nombrePredicate,
                apellidoPaternoPredicate,
                apellidoMaternoPredicate,
                ciPredicate,
                nombreCompletoPredicate,
                telefonoPredicate
            );
        };
    }

    public static Specification<Empleado> estadoEqual(Boolean estado) {
        return (root, query, cb) -> {
            if (estado == null) return null;
            return cb.equal(root.get("estado"), estado);
        };
    }

    public static Specification<Empleado> fechaContratacionBetween(String desde, String hasta) {
        return (root, query, cb) -> {
            if ((desde == null || desde.isBlank()) && (hasta == null || hasta.isBlank())) return null;

            LocalDate fechaDesde = (desde != null && !desde.isBlank()) ? LocalDate.parse(desde) : null;
            LocalDate fechaHasta = (hasta != null && !hasta.isBlank()) ? LocalDate.parse(hasta) : null;

            if (fechaDesde != null && fechaHasta != null) {
                return cb.between(root.get("fechaContratacion"), fechaDesde, fechaHasta);
            } else if (fechaDesde != null) {
                return cb.greaterThanOrEqualTo(root.get("fechaContratacion"), fechaDesde);
            } else {
                return cb.lessThanOrEqualTo(root.get("fechaContratacion"), fechaHasta);
            }
        };
    }
}
