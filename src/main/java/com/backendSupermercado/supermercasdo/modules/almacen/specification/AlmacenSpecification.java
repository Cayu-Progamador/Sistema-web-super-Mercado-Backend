package com.backendSupermercado.supermercasdo.modules.almacen.specification;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.almacen.entity.Almacen;

import jakarta.persistence.criteria.JoinType;

public final class AlmacenSpecification {

    private AlmacenSpecification() {
    }

    public static Specification<Almacen> buscar(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            String patron = "%" + search.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("codigo")), patron),
                    cb.like(cb.lower(root.get("nombre")), patron));
        };
    }

    public static Specification<Almacen> porTipo(String tipo) {
        return (root, query, cb) -> {
            if (tipo == null || tipo.isBlank()) return null;
            var tipoAlmacen = root.join("tipoAlmacen", JoinType.INNER);
            return cb.equal(cb.upper(tipoAlmacen.get("nombre")), tipo.trim().toUpperCase());
        };
    }

    public static Specification<Almacen> porEstado(Boolean estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("activo"), estado);
    }
}
