package com.backendSupermercado.supermercasdo.shared.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.Contrato;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class ContratoSpecification {

    public static Specification<Contrato> busquedaGeneral(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) return null;
            String pattern = "%" + termino.toLowerCase().trim() + "%";

            var empleadoJoin = root.join("empleado", JoinType.LEFT);
            var personaJoin = empleadoJoin.join("persona", JoinType.LEFT);

            Predicate nombrePredicate = cb.like(cb.lower(personaJoin.get("nombres")), pattern);
            Predicate apellidoPaternoPredicate = cb.like(cb.lower(personaJoin.get("apellidoPaterno")), pattern);
            Predicate apellidoMaternoPredicate = cb.like(cb.lower(personaJoin.get("apellidoMaterno")), pattern);
            Predicate ciPredicate = cb.like(cb.lower(personaJoin.get("ci")), pattern);

            Predicate nombreCompletoPredicate = cb.like(
                cb.lower(
                    cb.concat(
                        cb.concat(
                            cb.concat(personaJoin.get("nombres"), " "),
                            cb.coalesce(personaJoin.get("apellidoPaterno"), "")
                        ),
                        cb.concat(" ", cb.coalesce(personaJoin.get("apellidoMaterno"), ""))
                    )
                ),
                pattern
            );

            return cb.or(nombrePredicate, apellidoPaternoPredicate, apellidoMaternoPredicate, ciPredicate, nombreCompletoPredicate);
        };
    }

    public static Specification<Contrato> estadoEqual(String estado) {
        return (root, query, cb) -> {
            if (estado == null || estado.isBlank()) return null;
            return cb.equal(root.get("estado"), estado.toUpperCase());
        };
    }

    public static Specification<Contrato> controlaAsistenciaEqual(Boolean controlaAsistencia) {
        return (root, query, cb) -> {
            if (controlaAsistencia == null) return null;
            return cb.equal(root.get("controlaAsistencia"), controlaAsistencia);
        };
    }

    public static Specification<Contrato> tipoContratoNombreEqual(String tipoContratoNombre) {
        return (root, query, cb) -> {
            if (tipoContratoNombre == null || tipoContratoNombre.isBlank()) return null;
            return cb.equal(cb.lower(root.join("tipoContrato", JoinType.LEFT).get("nombre")), tipoContratoNombre.toLowerCase());
        };
    }

    public static Specification<Contrato> tipoJornadaNombreEqual(String tipoJornadaNombre) {
        return (root, query, cb) -> {
            if (tipoJornadaNombre == null || tipoJornadaNombre.isBlank()) return null;
            return cb.equal(cb.lower(root.join("tipoJornada", JoinType.LEFT).get("nombre")), tipoJornadaNombre.toLowerCase());
        };
    }

    public static Specification<Contrato> empleadoIdEqual(Long empleadoId) {
        return (root, query, cb) -> {
            if (empleadoId == null) return null;
            return cb.equal(root.get("empleado").get("idEmpleado"), empleadoId);
        };
    }

    public static Specification<Contrato> fechaInicioBetween(String desde, String hasta) {
        return (root, query, cb) -> {
            if ((desde == null || desde.isBlank()) && (hasta == null || hasta.isBlank())) return null;

            LocalDate fechaDesde = (desde != null && !desde.isBlank()) ? LocalDate.parse(desde) : null;
            LocalDate fechaHasta = (hasta != null && !hasta.isBlank()) ? LocalDate.parse(hasta) : null;

            if (fechaDesde != null && fechaHasta != null) {
                return cb.between(root.get("fechaInicio"), fechaDesde, fechaHasta);
            } else if (fechaDesde != null) {
                return cb.greaterThanOrEqualTo(root.get("fechaInicio"), fechaDesde);
            } else {
                return cb.lessThanOrEqualTo(root.get("fechaInicio"), fechaHasta);
            }
        };
    }

    public static Specification<Contrato> fechaFinVencido() {
        return (root, query, cb) ->
            cb.and(
                cb.isNotNull(root.get("fechaFin")),
                cb.lessThan(root.get("fechaFin"), LocalDate.now())
            );
    }

    public static Specification<Contrato> fechaFinBetween(String desde, String hasta) {
        return (root, query, cb) -> {
            if ((desde == null || desde.isBlank()) && (hasta == null || hasta.isBlank())) return null;

            LocalDate fechaDesde = (desde != null && !desde.isBlank()) ? LocalDate.parse(desde) : null;
            LocalDate fechaHasta = (hasta != null && !hasta.isBlank()) ? LocalDate.parse(hasta) : null;

            if (fechaDesde != null && fechaHasta != null) {
                return cb.between(root.get("fechaFin"), fechaDesde, fechaHasta);
            } else if (fechaDesde != null) {
                return cb.greaterThanOrEqualTo(root.get("fechaFin"), fechaDesde);
            } else {
                return cb.lessThanOrEqualTo(root.get("fechaFin"), fechaHasta);
            }
        };
    }
}
