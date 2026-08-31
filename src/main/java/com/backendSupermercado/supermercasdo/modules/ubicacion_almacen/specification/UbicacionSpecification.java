package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.specification;

import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;

public final class UbicacionSpecification {
    private UbicacionSpecification() { }
    public static Specification<UbicacionAlmacen> filtros(String search, Long idAlmacen, String tipo, Boolean estado) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();
            if (search != null && !search.isBlank()) {
                String patron = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                predicates = cb.and(predicates, cb.or(cb.like(cb.lower(root.get("codigo")), patron), cb.like(cb.lower(root.get("nombre")), patron)));
            }
            if (idAlmacen != null) predicates = cb.and(predicates, cb.equal(root.get("almacen").get("idAlmacen"), idAlmacen));
            if (tipo != null && !tipo.isBlank()) predicates = cb.and(predicates, cb.equal(cb.upper(root.get("tipo")), tipo.toUpperCase(Locale.ROOT)));
            if (estado != null) predicates = cb.and(predicates, cb.equal(root.get("activo"), estado));
            return predicates;
        };
    }
}
