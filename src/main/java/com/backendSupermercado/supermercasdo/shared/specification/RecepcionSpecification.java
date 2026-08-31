package com.backendSupermercado.supermercasdo.shared.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.recepcion.entity.RecepcionMercaderia;

import jakarta.persistence.criteria.JoinType;

public class RecepcionSpecification {

    private RecepcionSpecification() {
    }

    public static Specification<RecepcionMercaderia> estadoEqual(String estado) {
        return (root, query, cb) -> {
            if (estado == null || estado.isBlank()) return null;
            return cb.equal(root.get("estadoRecepcion").get("nombre"), estado);
        };
    }

    public static Specification<RecepcionMercaderia> proveedorEqual(Long idProveedor) {
        return (root, query, cb) -> {
            if (idProveedor == null) return null;
            return cb.equal(root.get("pedido").get("proveedor").get("idProveedor"), idProveedor);
        };
    }

    public static Specification<RecepcionMercaderia> fechaDesde(LocalDate fechaDesde) {
        return (root, query, cb) -> {
            if (fechaDesde == null) return null;
            return cb.greaterThanOrEqualTo(root.get("fechaRecepcion"), fechaDesde.atStartOfDay());
        };
    }

    public static Specification<RecepcionMercaderia> fechaHasta(LocalDate fechaHasta) {
        return (root, query, cb) -> {
            if (fechaHasta == null) return null;
            return cb.lessThanOrEqualTo(root.get("fechaRecepcion"), fechaHasta.plusDays(1).atStartOfDay());
        };
    }

    public static Specification<RecepcionMercaderia> busquedaGeneral(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            String like = "%" + search.trim().toLowerCase() + "%";

            var pedido = root.join("pedido");
            var proveedor = pedido.join("proveedor");
            var empresa = proveedor.join("empresa", JoinType.LEFT);
            var persona = proveedor.join("persona", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(empresa.get("razonSocial")), like),
                    cb.like(cb.lower(empresa.get("nombreComercial")), like),
                    cb.like(cb.lower(persona.get("nombres")), like));
        };
    }
}
