package com.backendSupermercado.supermercasdo.modules.inventario_fisico.specification;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.inventario_fisico.entity.DetalleInventarioFisico;

import jakarta.persistence.criteria.Predicate;

public final class DetalleInventarioFisicoSpecification {

    private DetalleInventarioFisicoSpecification() {
    }

    public static Specification<DetalleInventarioFisico> filtrar(
            Long idInventarioFisico, String buscar, Boolean soloPendientes, Boolean soloDiferencias) {
        return (root, query, cb) -> {
            Predicate predicate = cb.equal(
                    root.get("inventarioFisico").get("idInventarioFisico"), idInventarioFisico);

            if (buscar != null && !buscar.isBlank()) {
                String patron = "%" + buscar.trim().toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("producto").get("nombre")), patron),
                        cb.like(cb.lower(root.get("producto").get("codigoInterno")), patron),
                        cb.like(cb.lower(root.get("ubicacion").get("nombre")), patron),
                        cb.like(cb.lower(root.get("ubicacion").get("codigo")), patron)));
            }
            if (Boolean.TRUE.equals(soloPendientes)) {
                predicate = cb.and(predicate, cb.isNull(root.get("cantidadFisica")));
            }
            if (Boolean.TRUE.equals(soloDiferencias)) {
                predicate = cb.and(predicate,
                        cb.isNotNull(root.get("diferencia")),
                        cb.notEqual(root.get("diferencia"), 0));
            }
            return predicate;
        };
    }
}
