package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.entity.AjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.EstadoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.MotivoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.TipoAjusteInventario;

public final class AjusteInventarioSpecification {

    private AjusteInventarioSpecification() {
    }

    public static Specification<AjusteInventario> buscar(String texto) {
        return (root, query, cb) -> {
            if (texto == null || texto.isBlank()) return cb.conjunction();
            query.distinct(true);
            String patron = "%" + texto.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("codigo")), patron),
                    cb.like(cb.lower(root.get("observacion")), patron),
                    cb.like(cb.lower(root.get("creadoPor").get("username")), patron),
                    cb.like(cb.lower(root.join("detalles").join("producto").get("nombre")), patron));
        };
    }

    public static Specification<AjusteInventario> tipo(TipoAjusteInventario tipo) {
        return (root, query, cb) -> tipo == null ? cb.conjunction() : cb.equal(root.get("tipo"), tipo);
    }

    public static Specification<AjusteInventario> motivo(MotivoAjusteInventario motivo) {
        return (root, query, cb) -> motivo == null ? cb.conjunction() : cb.equal(root.get("motivo"), motivo);
    }

    public static Specification<AjusteInventario> estado(EstadoAjusteInventario estado) {
        return (root, query, cb) -> estado == null ? cb.conjunction() : cb.equal(root.get("estado"), estado);
    }

    public static Specification<AjusteInventario> fechaDesde(LocalDate fecha) {
        return (root, query, cb) -> fecha == null ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("fechaCreacion"), fecha.atStartOfDay());
    }

    public static Specification<AjusteInventario> fechaHasta(LocalDate fecha) {
        return (root, query, cb) -> fecha == null ? cb.conjunction()
                : cb.lessThan(root.get("fechaCreacion"), fecha.plusDays(1).atStartOfDay());
    }
}
