package com.backendSupermercado.supermercasdo.modules.transferencias_almacen.specification;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.entity.TransferenciaAlmacen;

public final class TransferenciaSpecification {
    private TransferenciaSpecification() { }

    public static Specification<TransferenciaAlmacen> filtros(String texto, String estado, Long usuarioId, String responsable,
            Long almacenOrigenId, Long almacenDestinoId, LocalDateTime desde, LocalDateTime hasta) {
        return (root, query, cb) -> {
            var p = cb.conjunction();
            if (texto != null && !texto.isBlank()) {
                String patron = "%" + texto.trim().toLowerCase(Locale.ROOT) + "%";
                var usuario = root.join("usuario");
                p = cb.and(p, cb.or(cb.like(cb.lower(root.get("codigo")), patron), cb.like(cb.lower(usuario.get("username")), patron)));
            }
            if (estado != null && !estado.isBlank()) p = cb.and(p, cb.equal(cb.upper(root.join("estadoTransferencia").get("nombre")), estado.toUpperCase(Locale.ROOT)));
            if (usuarioId != null) p = cb.and(p, cb.equal(root.join("usuario").get("idUsuario"), usuarioId));
            if (responsable != null && !responsable.isBlank()) p = cb.and(p, cb.like(cb.lower(root.join("usuario").get("username")), "%" + responsable.trim().toLowerCase(Locale.ROOT) + "%"));
            if (almacenOrigenId != null) p = cb.and(p, cb.equal(root.join("ubicacionOrigen").join("almacen").get("idAlmacen"), almacenOrigenId));
            if (almacenDestinoId != null) p = cb.and(p, cb.equal(root.join("ubicacionDestino").join("almacen").get("idAlmacen"), almacenDestinoId));
            if (desde != null) p = cb.and(p, cb.greaterThanOrEqualTo(root.get("fechaTransferencia"), desde));
            if (hasta != null) p = cb.and(p, cb.lessThan(root.get("fechaTransferencia"), hasta));
            return p;
        };
    }
}
