package com.backendSupermercado.supermercasdo.shared.specification;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class EmpleadoSpecification {

    public static Specification<Empleado> busquedaGeneral(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) return null;
            query.distinct(true);
            String pattern = "%" + termino.toLowerCase() + "%";

            var persona = root.join("persona", JoinType.LEFT);
            var cargo = root.join("cargo", JoinType.LEFT);
            var contactos = persona.join("contactos", JoinType.LEFT);

            Predicate nombrePredicate = cb.like(cb.lower(persona.get("nombres")), pattern);

            Predicate apellidoPaternoPredicate = cb.like(cb.lower(persona.get("apellidoPaterno")), pattern);

            Predicate apellidoMaternoPredicate = cb.like(cb.lower(persona.get("apellidoMaterno")), pattern);

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

            Predicate cargoPredicate = cb.like(cb.lower(cargo.get("nombre")), pattern);

            Predicate telefonoPredicate = cb.like(cb.lower(contactos.get("telefono")), pattern);

            return cb.or(
                nombrePredicate,
                apellidoPaternoPredicate,
                apellidoMaternoPredicate,
                nombreCompletoPredicate,
                cargoPredicate,
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
}
