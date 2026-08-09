package com.backendSupermercado.supermercasdo.shared.specification;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.proveedor.entity.Proveedor;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.TipoProveedor;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class ProveedorSpecification {

    public static Specification<Proveedor> busquedaGeneral(String termino) {
        return (root, query, cb) -> {
            if (termino == null || termino.isBlank()) return null;
            String pattern = "%" + termino.toLowerCase() + "%";

            var persona = root.join("persona", JoinType.LEFT);
            var empresa = root.join("empresa", JoinType.LEFT);

            Predicate nombreEmpresa = cb.like(cb.lower(cb.coalesce(empresa.get("razonSocial"), "")), pattern);
            Predicate nombreComercial = cb.like(cb.lower(cb.coalesce(empresa.get("nombreComercial"), "")), pattern);
            Predicate nit = cb.like(cb.lower(cb.coalesce(empresa.get("nit"), "")), pattern);

            Predicate nombres = cb.like(cb.lower(cb.coalesce(persona.get("nombres"), "")), pattern);
            Predicate apellidoPaterno = cb.like(cb.lower(cb.coalesce(persona.get("apellidoPaterno"), "")), pattern);
            Predicate apellidoMaterno = cb.like(cb.lower(cb.coalesce(persona.get("apellidoMaterno"), "")), pattern);
            Predicate ci = cb.like(cb.lower(cb.coalesce(persona.get("ci"), "")), pattern);

            return cb.or(
                nombreEmpresa, nombreComercial, nit,
                nombres, apellidoPaterno, apellidoMaterno, ci
            );
        };
    }

    public static Specification<Proveedor> tipoEqual(String tipo) {
        return (root, query, cb) -> {
            if (tipo == null || tipo.isBlank()) return null;
            return cb.equal(root.get("tipoProveedor"), TipoProveedor.valueOf(tipo.toUpperCase()));
        };
    }

    public static Specification<Proveedor> estadoEqual(Boolean estado) {
        return (root, query, cb) -> {
            if (estado == null) return null;
            return cb.equal(root.get("estado"), estado);
        };
    }

    public static Specification<Proveedor> ciudadEqual(Long idCiudad) {
        return (root, query, cb) -> {
            if (idCiudad == null) return null;
            var persona = root.join("persona", JoinType.LEFT);
            var empresa = root.join("empresa", JoinType.LEFT);
            var dirPersona = persona.join("direccion", JoinType.LEFT);
            var dirEmpresa = empresa.join("direccion", JoinType.LEFT);

            Predicate ciudadPersona = cb.equal(dirPersona.get("ciudad").get("idCiudad"), idCiudad);
            Predicate ciudadEmpresa = cb.equal(dirEmpresa.get("ciudad").get("idCiudad"), idCiudad);
            return cb.or(ciudadPersona, ciudadEmpresa);
        };
    }
}
