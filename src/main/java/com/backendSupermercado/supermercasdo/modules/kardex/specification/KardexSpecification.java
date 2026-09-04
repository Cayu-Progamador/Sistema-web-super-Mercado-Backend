package com.backendSupermercado.supermercasdo.modules.kardex.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.DetalleMovimientoAlmacen;

import jakarta.persistence.criteria.JoinType;

public final class KardexSpecification {

    private KardexSpecification() {
    }

    public static Specification<DetalleMovimientoAlmacen> buscar(String texto) {
        return (root, query, cb) -> {
            if (texto == null || texto.isBlank()) {
                return cb.conjunction();
            }

            String patron = "%" + texto.trim().toLowerCase() + "%";
            var producto = root.join("producto", JoinType.INNER);
            var movimiento = root.join("movimiento", JoinType.INNER);

            return cb.or(
                    cb.like(cb.lower(producto.get("codigoInterno")), patron),
                    cb.like(cb.lower(producto.get("nombre")), patron),
                    cb.like(cb.lower(movimiento.get("referencia")), patron));
        };
    }

    public static Specification<DetalleMovimientoAlmacen> producto(Long idProducto) {
        return (root, query, cb) -> idProducto == null
                ? cb.conjunction()
                : cb.equal(root.get("producto").get("idProducto"), idProducto);
    }

    public static Specification<DetalleMovimientoAlmacen> lote(Long idLote) {
        return (root, query, cb) -> idLote == null
                ? cb.conjunction()
                : cb.equal(root.get("lote").get("idLote"), idLote);
    }

    public static Specification<DetalleMovimientoAlmacen> fechaDesde(LocalDate fechaDesde) {
        return (root, query, cb) -> fechaDesde == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(
                        root.get("movimiento").get("fechaMovimiento"),
                        fechaDesde.atStartOfDay());
    }

    public static Specification<DetalleMovimientoAlmacen> fechaHasta(LocalDate fechaHasta) {
        return (root, query, cb) -> fechaHasta == null
                ? cb.conjunction()
                : cb.lessThan(
                        root.get("movimiento").get("fechaMovimiento"),
                        fechaHasta.plusDays(1).atStartOfDay());
    }
}
