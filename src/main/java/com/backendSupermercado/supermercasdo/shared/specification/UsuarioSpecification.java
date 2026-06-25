package com.backendSupermercado.supermercasdo.shared.specification;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class UsuarioSpecification {

    public static Specification<Usuario> busquedaGeneral(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) return null;
            query.distinct(true);
            String pattern = "%" + termino.toLowerCase() + "%";

            Predicate usernamePredicate = cb.like(cb.lower(root.get("username")), pattern);

            Join<?, ?> empleado = root.join("empleado", JoinType.LEFT);
            Join<?, ?> persona = empleado.join("persona", JoinType.LEFT);

            Predicate nombrePredicate = cb.or(
                cb.like(cb.lower(persona.get("nombres")), pattern),
                cb.like(cb.lower(persona.get("apellidoPaterno")), pattern),
                cb.like(cb.lower(persona.get("apellidoMaterno")), pattern)
            );

            return cb.or(usernamePredicate, nombrePredicate);
        };
    }

    public static Specification<Usuario> rolNombreEqual(String rolNombre) {
        return (root, query, cb) -> {
            if (rolNombre == null || rolNombre.isBlank()) return null;
            query.distinct(true);
            Join<?, ?> usuarioRoles = root.join("usuarioRoles", JoinType.INNER);
            Join<?, ?> rol = usuarioRoles.join("rol", JoinType.INNER);
            return cb.equal(cb.lower(rol.get("nombre")), rolNombre.toLowerCase());
        };
    }

    public static Specification<Usuario> activoEqual(Boolean activo) {
        return (root, query, cb) -> {
            if (activo == null) return null;
            return cb.equal(root.get("activo"), activo);
        };
    }

    public static Specification<Usuario> fechaCreacionBetween(LocalDate desde, LocalDate hasta) {
        return (root, query, cb) -> {
            if (desde == null && hasta == null) return null;

            var fechaDesde = desde != null ? desde.atStartOfDay() : null;
            var fechaHasta = hasta != null ? hasta.atTime(LocalTime.MAX) : null;

            if (fechaDesde != null && fechaHasta != null) {
                return cb.between(root.get("fechaCreacion"), fechaDesde, fechaHasta);
            }
            if (fechaDesde != null) {
                return cb.greaterThanOrEqualTo(root.get("fechaCreacion"), fechaDesde);
            }
            return cb.lessThanOrEqualTo(root.get("fechaCreacion"), fechaHasta);
        };
    }
}
