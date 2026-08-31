package com.backendSupermercado.supermercasdo.shared.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.pedidos.entity.PedidoProveedor;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class PedidoSpecification {

    public static Specification<PedidoProveedor> estadoEqual(String estado) {
        return (root, query, cb) -> {
            if (estado == null || estado.isBlank()) return null;
            return cb.equal(root.get("estadoPedido").get("nombre"), estado.toUpperCase());
        };
    }

    public static Specification<PedidoProveedor> proveedorEqual(Long idProveedor) {
        return (root, query, cb) -> {
            if (idProveedor == null) return null;
            return cb.equal(root.get("proveedor").get("idProveedor"), idProveedor);
        };
    }

    public static Specification<PedidoProveedor> fechaDesde(LocalDate fechaDesde) {
        return (root, query, cb) -> {
            if (fechaDesde == null) return null;
            return cb.greaterThanOrEqualTo(root.get("fechaPedido").as(LocalDate.class), fechaDesde);
        };
    }

    public static Specification<PedidoProveedor> fechaHasta(LocalDate fechaHasta) {
        return (root, query, cb) -> {
            if (fechaHasta == null) return null;
            return cb.lessThanOrEqualTo(root.get("fechaPedido").as(LocalDate.class), fechaHasta);
        };
    }

    public static Specification<PedidoProveedor> busquedaGeneral(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) return null;
            String pattern = "%" + termino.toLowerCase().trim() + "%";

            var proveedorJoin = root.join("proveedor", JoinType.LEFT);
            var personaJoin = proveedorJoin.join("persona", JoinType.LEFT);
            var empresaJoin = proveedorJoin.join("empresa", JoinType.LEFT);

            Predicate personaNombre = cb.like(cb.lower(cb.coalesce(personaJoin.get("nombres"), "")), pattern);
            Predicate apellidoPaterno = cb.like(cb.lower(cb.coalesce(personaJoin.get("apellidoPaterno"), "")), pattern);
            Predicate apellidoMaterno = cb.like(cb.lower(cb.coalesce(personaJoin.get("apellidoMaterno"), "")), pattern);
            Predicate razonSocial = cb.like(cb.lower(cb.coalesce(empresaJoin.get("razonSocial"), "")), pattern);
            Predicate nombreComercial = cb.like(cb.lower(cb.coalesce(empresaJoin.get("nombreComercial"), "")), pattern);

            return cb.or(personaNombre, apellidoPaterno, apellidoMaterno, razonSocial, nombreComercial);
        };
    }
}